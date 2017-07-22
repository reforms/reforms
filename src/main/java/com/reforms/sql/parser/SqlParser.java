package com.reforms.sql.parser;

import com.reforms.sql.expr.query.*;
import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.*;
import com.reforms.sql.expr.term.casee.CaseExpression;
import com.reforms.sql.expr.term.casee.ElseExpression;
import com.reforms.sql.expr.term.casee.WhenThenExpression;
import com.reforms.sql.expr.term.from.*;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.term.predicate.*;
import com.reforms.sql.expr.term.value.*;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ConditionFlowType.resolveConditionFlowType;
import static com.reforms.sql.expr.term.MathOperator.resolveMathOperator;
import static com.reforms.sql.expr.term.predicate.ComparisonOperator.COT_EQUALS;
import static com.reforms.sql.expr.term.predicate.ComparisonOperator.resolveComparisonOperatorType;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_LIMIT;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_OFFSET;
import static com.reforms.sql.parser.OptWords.*;
import static com.reforms.sql.parser.SqlWords.*;

/**
 * 1. ORACLE: SELECT selection_fields FROM (SELECT selection_fields,ROWNUM RN FROM (SELECT selection_fields FROM schemaName.tableName WHERE conditions ORDER BY orderFields)) WHERE RN > ? AND RN <= ?
 * 2. MSSQL: SELECT TOP 50 id, name FROM schemaName.tableName WHERE name LIKE ? AND __id__ NOT IN (SELECT TOP 100 __id__ FROM schemaName.tableName WHERE name LIKE ? ORDER BY id) ORDER BY id
 * TODO развитие: Добавить поддержку операторов INSERT, UPDATE, DELETE,
 *      развитие: Добавить поддержку хранимых процедур
 *      рефакторинг: Добавить парсинг служебных слов КАК есть без поднятия их к верхнему регистру для этого придется пролопатить Expression
 *
 * @author evgenie
 *
 * ИДЕИ:
 *  2 типа функций:
 *      - parse...
 *      - check...
 *  Смысловая часть:
 *      - query         (SelectQuery, InsertQuery, UpdateQuery, DeleteQuery, CallQuery)
 *      - statement     (SelectStatement, FromStatement, WhereStatement, ... etc)
 *      - expression    (AliasExpression, AsteriskExpression, ... etc)
 *      - word          (ALL, NOT, ... etc, part of some expression, but exclude main word)
 *      - operator      (MathOperator, ComparisonOperator)
 *
 * TODO времено код разбит на смысловые части не связанные между собой. Необходимо перегруппировать участки кода так, чтобы было видно общий смысл
 */
public class SqlParser {

    private final SqlStream stream;

    public SqlParser(String query) {
        stream = new SqlStream(query);
    }

    public SelectQuery parseSelectQuery() {
        SelectQuery directSelectQuery = parseVariantOfSelectQuery();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать SELECT запрос");
        }
        return directSelectQuery;
    }

    public UpdateQuery parseUpdateQuery() {
        UpdateStatement updateStatement = parseUpdateStatement();
        SetClauseStatement setClauseStatement = parseSetClauseStatement();
        WhereStatement whereStatement = parseWhereStatement();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать UPDATE запрос");
        }
        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setUpdateStatement(updateStatement);
        updateQuery.setSetClauseStatement(setClauseStatement);
        updateQuery.setWhereStatement(whereStatement);
        return updateQuery;
    }

    public DeleteQuery parseDeleteQuery() {
        DeleteStatement deleteStatement = parseDeleteStatement();
        FromStatement fromStatement = parseFromStatement();
        if (fromStatement == null) {
            throw stream.createException("Ожидается FROM секция в DELETE выражении");
        }
        UsingStatement usingStatement = parseUsingStatement();
        WhereStatement whereStatement = parseWhereStatement();
        OrderByStatement orderByStatement = parseOrderByStatement();
        LimitExpression limitExpr = parseLimitExpression();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать DELETE FROM запрос");
        }
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setDeleteStatement(deleteStatement);
        deleteQuery.setFromStatement(fromStatement);
        deleteQuery.setUsingStatement(usingStatement);
        deleteQuery.setWhereStatement(whereStatement);
        deleteQuery.setOrderByStatement(orderByStatement);
        deleteQuery.setLimitExpr(limitExpr);
        return deleteQuery;
    }

    public InsertQuery parseInsertQuery() {
        InsertStatement insertStatement = parseInsertStatement();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать INSERT INTO запрос");
        }
        InsertQuery insertQuery = new InsertQuery();
        insertQuery.setInsertStatement(insertStatement);
        return insertQuery;
    }

    public CallQuery parseCallQuery() {
        // {
        stream.checkIsOpenFigureParen(true);
        stream.moveCursor();
        QuestionExpression questionExpr = null;
        ValueListExpression valuesExpr = null;
        // ?
        if (checkIsQuestionExpression()) {
            int from = stream.getCursor();
            ValueExpression valueExpr = parseQuestionExpression();
            if (!(valueExpr instanceof QuestionExpression)) {
                throw stream.createException("Ожидается выражение типа 'QuestionExpression', а получено " +
                        (valueExpr == null ? "null" : valueExpr.getClass()), from);
            }
            questionExpr = (QuestionExpression) valueExpr;
            questionExpr.setSpacable(false);
            String eqValue = stream.parseComparisonOperatorValue();
            if (!"=".equals(eqValue)) {
                throw stream.createException("Ожидается оператор '='", from);
            }
        } else if (stream.checkIsOpenParent(false)) {
            valuesExpr = parseValueListExpression();
            valuesExpr.setSpacable(false);
            int from = stream.getCursor();
            String eqValue = stream.parseComparisonOperatorValue();
            if (!"=".equals(eqValue)) {
                throw stream.createException("Ожидается оператор '='", from);
            }
        }
        // call
        String callWord = stream.parseSpecialWordValueAndCheck(SW_CALL);
        // funcName(args)
        FuncExpression funcExpr = parseFuncExpression();
        // }
        stream.checkIsCloseFigureParen(true);
        stream.moveCursor();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать CALL запрос");
        }
        CallQuery callQuery = new CallQuery();
        callQuery.setQuestionExpr(questionExpr);
        callQuery.setValuesExpr(valuesExpr);
        callQuery.setJdbcView(valuesExpr == null);
        callQuery.setCallWord(callWord);
        callQuery.setFuncExpr(funcExpr);
        return callQuery;
    }

    /**
     * (SELECT ...) UNION ALL (SELECT )
     * @return SELECT-запрос
     */
    private SelectQuery parseVariantOfSelectQuery() {
        if (checkIsSubSelectQuery()) {
            SelectQuery subSelectQuery = parseSubSelectQuery();
            List<LinkingSelectQuery> linkingSelectQueries = parseLinkingSelectQueries();
            OrderByStatement orderByStatement = parseOrderByStatement();
            PageStatement pageStatement = parsePageStatement();
            if (linkingSelectQueries.isEmpty() && orderByStatement == null && pageStatement == null) {
                return subSelectQuery;
            }
            SelectQuery listOfSelectQuery = new SelectQuery();
            LinkingSelectQuery firstLinkingQuery = new LinkingSelectQuery();
            firstLinkingQuery.setLinkedSelectQuery(subSelectQuery);
            listOfSelectQuery.addLinkingQuery(firstLinkingQuery);
            for (LinkingSelectQuery otherLinkedQuery : linkingSelectQueries) {
                listOfSelectQuery.addLinkingQuery(otherLinkedQuery);
            }
            listOfSelectQuery.setOrderByStatement(orderByStatement);
            listOfSelectQuery.setPageStatement(pageStatement);
            return listOfSelectQuery;
        }
        return parseSingleSelectQuery();
    }

    private SelectQuery parseSingleSelectQuery() {
        SelectStatement selectStatement = parseSelectStatement();
        FromStatement fromStatement = parseFromStatement();
        WhereStatement whereStatement = parseWhereStatement();
        GroupByStatement groupByStatement = parseGroupByStatement();
        HavingStatement havingStatement = parseHavingStatement();
        List<LinkingSelectQuery> linkingSelectQueries = parseLinkingSelectQueries();
        OrderByStatement orderByStatement = parseOrderByStatement();
        PageStatement pageStatement = parsePageStatement();
        SelectQuery selectQuery = new SelectQuery();
        selectQuery.setSelectStatement(selectStatement);
        selectQuery.setFromStatement(fromStatement);
        selectQuery.setWhereStatement(whereStatement);
        selectQuery.setGroupByStatement(groupByStatement);
        selectQuery.setHavingStatement(havingStatement);
        selectQuery.setLinkingQueries(linkingSelectQueries);
        selectQuery.setOrderByStatement(orderByStatement);
        selectQuery.setPageStatement(pageStatement);
        return selectQuery;
    }

    private SelectStatement parseSelectStatement() {
        // 1. SELECT word
        String selectWord = stream.parseSpecialWordValueAndCheck(SW_SELECT);
        // 2. [ALL | DISTINCT] mode word
        String selectModeWord = parseSelectModeWord();
        // 3. [TOP]
        TopExpression topExpr = null;
        if (checkIsTopExpression()) {
            topExpr = parseTopExpression();
        }
        // 4. SELECT LIST EXPRS
        List<SelectableExpression> selectListExprs = parseSelectListExps();
        // 5. MAKE SELECT STATEMENT
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.setSelectExps(selectListExprs);
        selectStatement.setSelectWord(selectWord);
        selectStatement.setModeWord(selectModeWord);
        selectStatement.setCustomExpr(topExpr);
        return selectStatement;
    }

    private FromStatement parseFromStatement() {
        if (stream.checkIsSpecialWordValue()) {
            String fromWord = stream.parseSpecialWordValueAndCheck(SW_FROM);
            List<TableReferenceExpression> tableRefExprs = parseTableReferenceExpressions(SW_FROM);
            FromStatement fromStatement = new FromStatement();
            fromStatement.setFromWord(fromWord);
            fromStatement.setTableRefExprs(tableRefExprs);
            return fromStatement;
        }
        return null;
    }

    private UsingStatement parseUsingStatement() {
        if (stream.checkIsSpecialWordValueSame(SW_USING)) {
            String usingWord = stream.parseSpecialWordValueAndCheck(SW_USING);
            List<TableReferenceExpression> tableRefExprs = parseTableReferenceExpressions(SW_USING);
            UsingStatement usingStatement = new UsingStatement();
            usingStatement.setUsingWord(usingWord);
            usingStatement.setTableRefExprs(tableRefExprs);
            return usingStatement;
        }
        return null;
    }

    private List<TableReferenceExpression> parseTableReferenceExpressions(String statementName) {
        List<TableReferenceExpression> tableRefExprs = new ArrayList<>();
        while (checkIsTableReferenceExpression(!tableRefExprs.isEmpty())) {
            TableReferenceExpression tableRefExpr = parseTableReferenceExpression(!tableRefExprs.isEmpty());
            tableRefExprs.add(tableRefExpr);
            if (stream.checkIsComma()) {
                tableRefExpr.setSeparator(", ");
                stream.moveCursor();
            } else if (tableRefExprs.size() > 1) {
                int prevIndex = tableRefExprs.size() - 2;
                TableReferenceExpression prevTableRefExpr = tableRefExprs.get(prevIndex);
                if (prevTableRefExpr.getSeparator() == null) {
                    prevTableRefExpr.setSeparator(" ");
                }
            }
        }
        if (tableRefExprs.isEmpty()) {
            throw stream.createException("Ожидается в секции '" + statementName + "' блок данных о таблицах");
        }
        return tableRefExprs;
    }

    private WhereStatement parseWhereStatement() {
        if (checkIsWhereStatement()) {
            String whereWord = stream.parseSpecialWordValueAndCheck(SW_WHERE);
            Expression searchExpr = parseFullSearchConditionsExpr();
            WhereStatement whereStatementExpr = new WhereStatement();
            whereStatementExpr.setWhereWord(whereWord);
            whereStatementExpr.setSearchExpr(searchExpr);
            return whereStatementExpr;
        }
        return null;
    }

    private GroupByStatement parseGroupByStatement() {
        if (checkIsGroupByStatement()) {
            String groupWord = stream.parseSpecialWordValueAndCheck(SW_GROUP);
            String byWord = stream.parseSpecialWordValueAndCheck(SW_BY);
            GroupByStatement groupByStatement = new GroupByStatement();
            groupByStatement.setGroupWord(groupWord);
            groupByStatement.setByWord(byWord);
            GroupingColumnReferenceExpression firstRefColumnExpr = parseGroupingColumnReferenceExpression();
            groupByStatement.addGroupByExpr(firstRefColumnExpr);
            while (stream.checkIsComma()) {
                stream.moveCursor();
                GroupingColumnReferenceExpression refColumnExpr = parseGroupingColumnReferenceExpression();
                groupByStatement.addGroupByExpr(refColumnExpr);
            }
            return groupByStatement;
        }
        return null;
    }

    private HavingStatement parseHavingStatement() {
        if (checkIsHavingStatement()) {
            String havingWord = stream.parseSpecialWordValueAndCheck(SW_HAVING);
            Expression searchExpr = parseFullSearchConditionsExpr();
            HavingStatement havingStatementExpr = new HavingStatement();
            havingStatementExpr.setHavingWord(havingWord);
            havingStatementExpr.setSearchExpr(searchExpr);
            return havingStatementExpr;
        }
        return null;
    }

    private List<LinkingSelectQuery> parseLinkingSelectQueries() {
        List<LinkingSelectQuery> linkingSelectQueries = new ArrayList<>();
        while (checkIsLinkedWord()) {
            LinkingSelectQuery linkedSelectExpr = new LinkingSelectQuery();
            String linkedWord = parseLinkedWord();
            linkedSelectExpr.setLinkedWord(linkedWord);
            if (checkIsAllWord()) {
                String allWord = parseAllWord();
                linkedSelectExpr.setAllWord(allWord);
            }
            SelectQuery linkedSelectQuery = parseVariantOfSelectQuery();
            linkedSelectExpr.setLinkedSelectQuery(linkedSelectQuery);
            linkingSelectQueries.add(linkedSelectExpr);
        }
        return linkingSelectQueries;
    }

    private OrderByStatement parseOrderByStatement() {
        if (checkIsOrderByStatement()) {
            String orderWord = stream.parseSpecialWordValueAndCheck(SW_ORDER);
            String byWord = stream.parseSpecialWordValueAndCheck(SW_BY);
            List<SortKeyExpression> sortKeyExprs = parseSortKeyExpressions();
            OrderByStatement orderByStatement = new OrderByStatement();
            orderByStatement.setOrderWord(orderWord);
            orderByStatement.setByWord(byWord);
            orderByStatement.setSortKeyExprs(sortKeyExprs);
            return orderByStatement;
        }
        return null;
    }

    private FetchStatement parseFetchStatement() {
        if (checkIsFetchStatement()) {
            String fetchWord = stream.parseSpecialWordValueAndCheck(SW_FETCH);
            String portionWord = stream.parseSpecialWordValueAndCheckOneOf(SW_FIRST, SW_NEXT);
            Expression valueExpr = parseSimpleArgExpression();
            String rowsWord = stream.parseSpecialWordValueAndCheckOneOf(SW_ROWS, SW_ROW);
            String onlyWord = stream.parseSpecialWordValueAndCheck(SW_ONLY);
            FetchStatement fetchStatement = new FetchStatement();
            fetchStatement.setFetchWord(fetchWord);
            fetchStatement.setPortionWord(portionWord);
            fetchStatement.setValueExpr(valueExpr);
            fetchStatement.setRowsWord(rowsWord);
            fetchStatement.setOnlyWord(onlyWord);
            return fetchStatement;
        }
        return null;
    }

    private PartitionByStatement parsePartitionByStatement() {
        if (checkIsPartitionByStatement()) {
            String partitionWord = stream.parseSpecialWordValueAndCheck(SW_PARTITION);
            String byWord = stream.parseSpecialWordValueAndCheck(SW_BY);
            List<Expression> valueExprs = parseValueExpressions(true);
            PartitionByStatement partitionByStatement = new PartitionByStatement();
            partitionByStatement.setPartitionWord(partitionWord);
            partitionByStatement.setByWord(byWord);
            partitionByStatement.setValueExprs(valueExprs);
            return partitionByStatement;
        }
        return null;
    }

    private OverStatement parseOverStatement() {
        if (stream.checkIsSpecialWordValueSame(SW_OVER)) {
            String overWord = stream.parseSpecialWordValueAndCheck(SW_OVER);
            stream.checkIsOpenParent(true);
            stream.moveCursor();
            PartitionByStatement partitionByStatement = parsePartitionByStatement();
            OrderByStatement orderByStatement = parseOrderByStatement();
            stream.checkIsCloseParen(true);
            stream.moveCursor();
            OverStatement overStatement = new OverStatement();
            overStatement.setOverWord(overWord);
            overStatement.setPartitionByStatement(partitionByStatement);
            overStatement.setOrderByStatement(orderByStatement);
            return overStatement;
        }
        return null;
    }

    private PageStatement parsePageStatement() {
        LimitExpression limitExpr = parseLimitExpression();
        OffsetExpression offsetExpr = parseOffsetExpression();
        FetchStatement fetchStatement = parseFetchStatement();
        if (offsetExpr != null && limitExpr == null) {
            limitExpr = parseLimitExpression();
        }
        if (limitExpr == null && offsetExpr == null) {
            return null;
        }
        PageStatement pageStatement = new PageStatement();
        pageStatement.setLimitExpr(limitExpr);
        pageStatement.setOffsetExpr(offsetExpr);
        pageStatement.setFetchStatement(fetchStatement);
        return pageStatement;
    }

    private UpdateStatement parseUpdateStatement() {
        String updateWord = stream.parseSpecialWordValueAndCheck(SW_UPDATE);
        TableExpression tableExpr = parseTableExpression();
        UpdateStatement updateStatement = new UpdateStatement();
        updateStatement.setUpdateWord(updateWord);
        updateStatement.setTableExpr(tableExpr);
        return updateStatement;
    }

    private SetClauseStatement parseSetClauseStatement() {
        String setWord = stream.parseSpecialWordValueAndCheck(SW_SET);
        List<SetClauseExpression> setClauseList = parseSetClauseExpressions();
        SetClauseStatement setClauseStatement = new SetClauseStatement();
        setClauseStatement.setSetWord(setWord);
        setClauseStatement.setSetClauseList(setClauseList);
        return setClauseStatement;
    }

    /** TODO для мускула между DELETE FROM может быть выражение имени DELETE aliasName FROM tableName as aliasName */
    private DeleteStatement parseDeleteStatement() {
        String deleteWord = stream.parseSpecialWordValueAndCheck(SW_DELETE);
        List<TableExpression> tableExprs = new ArrayList<>();
        if (checkIsTableExpression()) {
            TableExpression tableExpr = parseTableExpression();
            tableExprs.add(tableExpr);
            while (stream.checkIsComma()) {
                stream.moveCursor();
                tableExpr = parseTableExpression();
                tableExprs.add(tableExpr);
            }
        }
        DeleteStatement deleteStatement = new DeleteStatement();
        deleteStatement.setDeleteWord(deleteWord);
        deleteStatement.setTableExprs(tableExprs);
        return deleteStatement;
    }

    private InsertStatement parseInsertStatement() {
        String insertIntoWords = stream.parseSpecialWordSequents(OW_R_INSERT, OW_R_INTO);
        if (insertIntoWords == null) {
            throw stream.createException("Ожидается INSERT INTO");
        }
        TableExpression tableExpr = parseTableExpression();
        ValueListExpression insertColumnNamesExpr = parseInsertColumnNamesExpression();
        Expression insertValuesExpr = parseInsertValuesExpression();
        InsertStatement insertStatement = new InsertStatement();
        insertStatement.setInsertIntoWords(insertIntoWords);
        insertStatement.setTableExpr(tableExpr);
        insertStatement.setInsertColumnNamesExpr(insertColumnNamesExpr);
        insertStatement.setInsertValuesExpr(insertValuesExpr);
        return insertStatement;
    }

    private ValueListExpression parseInsertColumnNamesExpression() {
        stream.keepParserState();
        if (stream.checkIsOpenParent(false)) {
            if (checkIsSubSelectQuery()) {
                stream.rollbackParserState();
                return null;
            }
            stream.rollbackParserState();
            return parseValueListExpression();
        }
        stream.rollbackParserState();
        return null;
    }

    private Expression parseInsertValuesExpression() {
        if (checkIsInsertSimpleValuesExpression()) {
            return parseInsertSimpleValuesExpression();
        }
        return parseVariantOfSelectQuery();
    }

    private boolean checkIsInsertSimpleValuesExpression() {
        return stream.checkIsSpecialWordValueSame(SW_VALUES);
    }

    private ValuesExpression parseInsertSimpleValuesExpression() {
        String valuesWord = stream.parseSpecialWordValueAndCheck(SW_VALUES);
        ValueListExpression valueListExpr = parseValueListExpression();
        ValuesExpression valuesExpr = new ValuesExpression();
        valuesExpr.setValuesWord(valuesWord);
        valuesExpr.setValueListExpr(valueListExpr);
        return valuesExpr;
    }

    private List<SetClauseExpression> parseSetClauseExpressions() {
        if (!checkIsColumnExpression()) {
            throw stream.createException("Ожидается наименование колонки для установки значения в SET выражении");
        }
        List<SetClauseExpression> setClauseList = new ArrayList<>();
        boolean clauseListFlag = true;
        while (clauseListFlag) {
            ColumnExpression columnExpr = parseColumnExpression();
            int from = stream.getCursor();
            ComparisonOperator compOperator = parseComparisonOperatorType();
            if (COT_EQUALS != compOperator) {
                throw stream.createException("Ожидается оператор '='", from);
            }
            Expression updateValueExpr = parseFullSingleRowValueConstructorExpr();
            SetClauseExpression setClauseExpr = new SetClauseExpression();
            setClauseExpr.setColumnExpr(columnExpr);
            setClauseExpr.setUpdateValueExpr(updateValueExpr);
            setClauseList.add(setClauseExpr);
            clauseListFlag = stream.checkIsComma();
            if (clauseListFlag) {
                stream.moveCursor();
            }
        }
        return setClauseList;
    }

    private List<SelectableExpression> parseSelectListExps() {
        List<SelectableExpression> selectListExprs = new ArrayList<>();
        SelectableExpression selectExpr = parseFullSelectableExpression();
        if (selectExpr == null) {
            throw stream.createException("В выражении SELECT должен быть хотя бы 1 параметр выборки");
        }
        selectListExprs.add(selectExpr);
        while (stream.checkIsComma()) {
            stream.moveCursor();
            int from = stream.getCursor();
            selectExpr = parseFullSelectableExpression();
            if (selectExpr == null) {
                throw stream.createException("В выражении SELECT ожидается параметр выборки", from);
            }
            selectListExprs.add(selectExpr);
        }
        return selectListExprs;
    }

    private SelectableExpression parseFullSelectableExpression() {
        stream.skipSpaces();
        SelectableExpression selectExpr = parseSingleSelectExpression();
        if (selectExpr != null) {
            AsClauseExpression asClauseExpr = parseAsClauseExpression(true);
            if (asClauseExpr != null) {
                if (selectExpr instanceof ExtendsSelectableExpression) {
                    ExtendsSelectableExpression extSelectableExpr = (ExtendsSelectableExpression) selectExpr;
                    extSelectableExpr.setAsClauseExpr(asClauseExpr);
                    selectExpr = extSelectableExpr;
                } else {
                    ExtendsSelectableExpression extSelectableExpr = new ExtendsSelectableExpression();
                    extSelectableExpr.setPrimaryExpr(selectExpr);
                    extSelectableExpr.setAsClauseExpr(asClauseExpr);
                    selectExpr = extSelectableExpr;
                }
            }
        }
        return selectExpr;
    }

    /** TODO переписать */
    private SelectableExpression parseSingleSelectExpression() {
        int from = stream.getCursor();
        boolean expressionState = true;
        ParenLevels levels = new ParenLevels();
        stream.skipSpaces();
        if (!isOpenBrace()) {
            levels.push(new ParenLevel(false));
        }
        while (true) {
            stream.skipSpaces();
            if (isOpenBrace()) {
                levels.incDepth();
                levels.push(new ParenLevel(true));
                stream.moveCursor();
                continue;
            }
            if (stream.checkIsCloseParen(false)) {
                levels.decDepth();
                if (!levels.isEmpty()) {
                    ParenLevel checkedLevel = levels.peek();
                    if (!checkedLevel.isUseParen()) {
                        levels.incDepth();
                        break;
                    }
                }
                if (levels.getDepth() < 0) {
                    throw stream.createException(
                            "Ошибка при разборе выборки. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                stream.moveCursor();
                ParenLevel currentLevel = levels.pop();
                ParenLevel parentLevel = null;
                if (levels.isEmpty()) {
                    parentLevel = new ParenLevel(false);
                } else {
                    parentLevel = levels.pop();
                }
                Expression layerExpression = currentLevel.combine(true);
                ExtendsSelectableExpression extSelectableExpr = parseExtendsSelectableExpression();
                if (extSelectableExpr != null) {
                    extSelectableExpr.setPrimaryExpr(layerExpression);
                    layerExpression = extSelectableExpr;
                }
                parentLevel.add(layerExpression);
                levels.push(parentLevel);
                continue;
            }
            if (expressionState) {
                Expression expr = parseSelectableWithExtraInfoExpression();
                ParenLevel currentLevel = levels.peek();
                currentLevel.add(expr);
                expressionState = false;
                continue;
            }
            if (!checkIsMathOperator()) {
                break;
            }
            MathOperator mathOperator = parseMathOperator();
            ParenLevel currentLevel = levels.peek();
            currentLevel.add(mathOperator);
            expressionState = true;
        }
        if (levels.getDepth() != 0) {
            throw stream.createException("Ошибка при разборе выборки. Количество закрывающих скобок ')' меньше, чем открывающихся '('",
                    from);
        }
        if (levels.size() != 1) {
            throw stream.createException("Ошибка при разборе выборки. Количество вложений '" + levels.size() + "'", from);
        }
        ParenLevel rootLevel = levels.pop();
        SelectableExpression searchExprs = (SelectableExpression) rootLevel.combine(false);
        return searchExprs;
    }

    /** TODO переписать */
    private AsClauseExpression parseAsClauseExpression(boolean selectExpr) {
        stream.skipSpaces();
        stream.keepParserState();
        AsClauseExpression asClauseExpr = new AsClauseExpression();
        String word = selectExpr ? stream.parseMetaIdentifierValue() : stream.parseIdentifierValue();
        if (word == null) {
            word = stream.parseDoubleQuoteValue();
        }
        if (SW_AS.equalsIgnoreCase(word)) {
            asClauseExpr.setAsWord(word);
            int from = stream.getCursor();
            word = selectExpr ? stream.parseMetaIdentifierValue() : stream.parseIdentifierValue();
            if (word == null) {
                word = stream.parseDoubleQuoteValue();
                if (word == null) {
                    throw stream.createException("После ключеого слова 'AS' ожидается алиас", from);
                }
            }
            stream.skipParserState();
            asClauseExpr.setAlias(word);
            return asClauseExpr;
        } else if (!isSqlWord(word) && word != null) {
            stream.skipParserState();
            asClauseExpr.setAlias(word);
            return asClauseExpr;
        }
        stream.rollbackParserState();
        return null;
    }

    private SelectableExpression parseSelectableWithExtraInfoExpression() {
        SelectableExpression primaryExpr = parseSelectableExpression();
        ExtendsSelectableExpression extSelectableExpr = parseExtendsSelectableExpression();
        if (extSelectableExpr != null) {
            extSelectableExpr.setPrimaryExpr(primaryExpr);
            return extSelectableExpr;
        }
        return primaryExpr;
    }

    private ExtendsSelectableExpression parseExtendsSelectableExpression() {
        TimeZoneExpression timeZoneExpr = null;
        if (checkIsTimeZoneExpression()) {
            timeZoneExpr = parseTimeZoneExpression();
        }
        TypeCastExpression typeCastExpr = null;
        if (checkIsTypeCastExpression()) {
            typeCastExpr = parseTypeCastExpression();
        }
        ExtendsSelectableExpression extSelectableExpr = null;
        if (timeZoneExpr != null || typeCastExpr != null) {
            extSelectableExpr = new ExtendsSelectableExpression();
            extSelectableExpr.setTimeZoneExpr(timeZoneExpr);
            extSelectableExpr.setTypeCastExpr(typeCastExpr);
            return extSelectableExpr;
        }
        return null;
    }

    private boolean checkIsTimeZoneExpression() {
        return stream.checkIsSpecialWordSequents(OW_R_AT, OW_R_TIME, OW_R_ZONE);
    }

    private TimeZoneExpression parseTimeZoneExpression() {
        String atTimeZonePhrase = stream.parseSpecialWordSequents(OW_R_AT, OW_R_TIME, OW_R_ZONE);
        if (atTimeZonePhrase == null) {
            throw stream.createException("Ожидается фраза 'AT TIME ZONE'");
        }
        Expression timeZoneNameExpr = parseSelectableExpression();
        TimeZoneExpression timeZoneExpr = new TimeZoneExpression();
        timeZoneExpr.setAtTimeZonePhrase(atTimeZonePhrase);
        timeZoneExpr.setTimeZoneNameExpr(timeZoneNameExpr);
        return timeZoneExpr;
    }

    private boolean checkIsTypeCastExpression() {
        return stream.checkIsDoubleColon();
    }

    private TypeCastExpression parseTypeCastExpression() {
        if (!checkIsTypeCastExpression()) {
            throw stream.createException("Ожидается символы '::'");
        }
        stream.moveCursor(2);
        FuncExpression typeInfoExpr = parseFuncVariantExpression();
        TypeCastExpression typeCastExpr = new TypeCastExpression();
        typeCastExpr.setTypeInfoExpr(typeInfoExpr);
        return typeCastExpr;
    }

    private SelectableExpression parseSelectableExpression() {
        // 1
        if (checkIsAsteriskExpression()) {
            return parseAsteriskExpression();
        }
        // 2
        if (checkIsFuncExpression()) {
            return parseFuncExpression();
        }
        // 3
        if (checkIsValueExpression()) {
            return parseValueExpression();
        }
        // 4
        if (checkIsCaseExpression()) {
            return parseCaseExpression();
        }
        // 5
        if (checkIsCastExpression()) {
            return parseCastExpression();
        }
        // 6
        if (checkIsSubSelectQuery()) {
            return parseSubSelectQuery();
        }
        // 7
        if (checkIsColumnExpression()) {
            return parseColumnExpression();
        }
        throw stream.createException("Неизвестное выражение для выборки");
    }

    private boolean checkIsTopExpression() {
        return stream.checkIsSpecialWordValueSame(SW_TOP);
    }

    private TopExpression parseTopExpression() {
        String topWord = stream.parseSpecialWordValueAndCheck(SW_TOP);
        boolean argFlag = false;
        if (stream.checkIsOpenParent(false)) {
            argFlag = true;
            stream.moveCursor();
        }
        Expression valueExpr = parseSelectableExpression();
        if (argFlag) {
            stream.checkIsCloseParen();
            stream.moveCursor();
        }
        String percentWord = stream.parseSpecialWordValueVariants(SW_PERCENT);
        String withTiesWords = null;
        int from = stream.getCursor();
        if (checkIsWithWord()) {
            withTiesWords = stream.parseSpecialWordSequents(OW_R_WITH, OW_R_TIES);
            if (withTiesWords == null) {
                throw stream.createException("Неизвестное выражение при разборе TOP части. Ожидается 'WITH TIES'.", from);
            }
        }
        TopExpression topExpr = new TopExpression();
        topExpr.setTopWord(topWord);
        topExpr.setArgFlag(argFlag);
        topExpr.setExpression(valueExpr);
        topExpr.setPercentWord(percentWord);
        topExpr.setWithTiesWords(withTiesWords);
        return topExpr;
    }

    private boolean checkIsWithWord() {
        return stream.checkIsSpecialWordValueSame(SW_WITH);
    }

    // 1
    private boolean checkIsAsteriskExpression() {
        return stream.checkIsAsteriskValue();
    }

    // 1
    private AsteriskExpression parseAsteriskExpression() {
        stream.parseAsteriskValueAndCheck();
        return new AsteriskExpression();
    }


    // 2
    private boolean checkIsFuncExpression() {
        stream.keepParserState();
        String id = stream.parseSpecialWordValue();
        boolean funcFlag = id != null && !isSqlWord(id) && stream.checkIsOpenParent(false);
        stream.rollbackParserState();
        return funcFlag;
    }

    // 2
    private FuncExpression parseFuncExpression() {
        return parseCommonFuncExpression();
    }

    private FuncExpression parseCommonFuncExpression() {
        String id3 = null; // schemaName
        String id2 = null; // spaceName
        String id1 = stream.parseSpecialWordValue(); // funcName (inverse order)
        if (id1 == null) {
            throw stream.createException("Ожидается наименование функции");
        }
        if (stream.checkIsDot(false)) {
            stream.moveCursor();
            id3 = id1; // set schema name
            id1 = stream.parseSpecialWordValue();
            if (id1 == null) {
                throw stream.createException("Ожидается наименование функции и пространство имен");
            }
            if (stream.checkIsDot(false)) {
                stream.moveCursor();
                id2 = id1; // set space name
                id1 = stream.parseSpecialWordValue();
                if (id1 == null) {
                    throw stream.createException("Ожидается наименование функции");
                }
            }
        }
        ValueListExpression funcArgs = parseFuncArgListExpression();
        OverStatement overStatement = parseOverStatement();
        FuncExpression funcExpr = new FuncExpression();
        funcExpr.setSchemeName(id3);
        funcExpr.setSpaceName(id2);
        funcExpr.setName(id1);
        funcExpr.setArgs(funcArgs);
        funcExpr.setOverStatement(overStatement);
        return funcExpr;
    }

    // 2.1
    private ValueListExpression parseFuncArgListExpression() {
        return parseValueListOrArgListExpression(false);
    }

    // 2.1
    private ValueListExpression parseValueListExpression() {
        return parseValueListOrArgListExpression(true);
    }

    // 2.1
    private ValueListExpression parseValueListOrArgListExpression(boolean isValue) {
        ValueListExpression argListExpr = new ValueListExpression();
        stream.checkIsOpenParent();
        stream.moveCursor();
        if (!stream.checkIsCloseParen(false)) {
            argListExpr.setValueExprs(parseValueExpressions(isValue));
        }
        stream.checkIsCloseParen();
        stream.moveCursor();
        return argListExpr;
    }

    // 2.1
    private ValueListExpression parseValueListExpressionFromSecondExpression(boolean simpleArg, Expression firstArgExpr) {
        ValueListExpression argListExpr = new ValueListExpression();
        argListExpr.addValueExpr(firstArgExpr);
        while (stream.checkIsComma()) {
            stream.moveCursor();
            Expression argExpr = parseValueOrArgExpression(simpleArg);
            argListExpr.addValueExpr(argExpr);
        }
        stream.checkIsCloseParen();
        stream.moveCursor();
        return argListExpr;
    }

    private List<Expression> parseValueExpressions(boolean isValue) {
        List<Expression> exprs = new ArrayList<>();
        boolean hasNextValue = true;
        while (hasNextValue) {
            Expression argExpr = parseValueOrArgExpression(isValue);
            exprs.add(argExpr);
            hasNextValue = stream.checkIsComma();
            if (hasNextValue) {
                stream.moveCursor();
            }
        }
        return exprs;
    }

    // 2.1.1
    private Expression parseValueOrArgExpression(boolean isValue) {
        if (isValue) {
            return parseSimpleArgExpression();
        }
        return parseFuncArgExpression();
    }

    // 2.1.2
    private SelectableExpression parseSimpleArgExpression() {
        SelectableExpression argValueExpr = parseSingleSelectExpression();
        return argValueExpr;
    }

    // 2.1.3
    private ArgExpression parseFuncArgExpression() {
        String specialWord = parseSelectModeWord();
        SelectableExpression argValueExpr = parseSingleSelectExpression();
        ArgExpression funcArgExpr = new ArgExpression();
        funcArgExpr.setSpecialWord(specialWord);
        funcArgExpr.setArgValueExpr(argValueExpr);
        return funcArgExpr;
    }

    // 2.2
    private FuncExpression parseFuncVariantExpression() {
        FuncExpression funcExpr = null;
        if (checkIsFuncExpression()) {
            funcExpr = parseFuncExpression();
        } else {
            String typeName = stream.parseSpecialWordValue();
            if (typeName == null) {
                throw stream.createException("Ожидается имя типа или функции");
            }
            funcExpr = new FuncExpression();
            funcExpr.setName(typeName);
            funcExpr.setShortStyle(true);
        }
        return funcExpr;
    }

    // 3
    private boolean checkIsValueExpression() {
        // 3.1
        if (checkIsNumericExpression()) {
            return true;
        }
        // 3.2
        if (checkIsStringExpression()) {
            return true;
        }
        // 3.3.1
        if (checkIsDateExpression()) {
            return true;
        }
        // 3.3.2
        if (checkIsDateJdbcExpression()) {
            return true;
        }
        // 3.4
        if (checkIsConstExpression()) {
            return true;
        }
        // 3.5
        if (checkIsQuestionExpression()) {
            return true;
        }
        // 3.6
        if (checkIsFilterExpression()) {
            return true;
        }
        return false;
    }

    // 3
    private ValueExpression parseValueExpression() {
        // 3.1
        if (checkIsNumericExpression()) {
            return parseNumericExpression();
        }
        // 3.2
        if (checkIsStringExpression()) {
            return parseStringExpression();
        }
        // 3.3.1
        if (checkIsDateExpression()) {
            return parseDateExpression();
        }
        // 3.3.2
        if (checkIsDateJdbcExpression()) {
            return parseDateJdbcExpression();
        }
        // 3.4
        if (checkIsConstExpression()) {
            return parseConstExpression();
        }
        // 3.5
        if (checkIsQuestionExpression()) {
            return parseQuestionExpression();
        }
        // 3.6
        if (checkIsFilterExpression()) {
            return parseFilterExpression();
        }
        throw stream.createException("Ожидается типовое значение");
    }

    // 3.1
    private boolean checkIsNumericExpression() {
        return stream.checkIsNumericValue();
    }

    // 3.1
    private NumericExpression parseNumericExpression() {
        int from = stream.getCursor();
        String numericValue = stream.parseNumericValue();
        if (numericValue == null) {
            throw stream.createException("Ожидается числовое значение", from);
        }
        return new NumericExpression(numericValue);
    }

    // 3.2
    private boolean checkIsStringExpression() {
        return stream.checkIsStringValue();
    }

    // 3.2
    private StringExpression parseStringExpression() {
        int from = stream.getCursor();
        String stringValue = stream.parseStringValue();
        if (stringValue == null) {
            throw stream.createException("Ожидается строковое значение", from);
        }
        return new StringExpression(stringValue);
    }

    // 3.3.1
    private boolean checkIsDateExpression() {
        return stream.checkIsSpecialWordValueOneOf(SW_TIME, SW_DATE, SW_TIMESTAMP, SW_INTERVAL);
    }

    // 3.3.1
    private ValueExpression parseDateExpression() {
        int from = stream.getCursor();
        String dateWord = stream.parseSpecialWordValueAndCheckOneOf(SW_TIME, SW_DATE, SW_TIMESTAMP, SW_INTERVAL);
        StringExpression dateValue = parseStringExpression();
        if (SW_TIME.equalsIgnoreCase(dateWord)) {
            return new TimeExpression(dateWord, dateValue.getValue(), false);
        }
        if (SW_DATE.equalsIgnoreCase(dateWord)) {
            return new DateExpression(dateWord, dateValue.getValue(), false);
        }
        if (SW_TIMESTAMP.equalsIgnoreCase(dateWord)) {
            return new TimestampExpression(dateWord, dateValue.getValue(), false);
        }
        if (SW_INTERVAL.equalsIgnoreCase(dateWord)) {
            return new IntervalExpression(dateWord, dateValue.getValue());
        }
        throw stream.createException("Не известное выражение типа даты: '" + dateWord + "', value: '" + dateValue.getValue() + "'", from);
    }

    private boolean checkIsDateJdbcExpression() {
        return stream.checkIsOpenFigureParen(false);
    }

    private ValueExpression parseDateJdbcExpression() {
        stream.checkIsOpenFigureParen(true);
        stream.moveCursor();
        int from = stream.getCursor();
        String dateTimeAlias = stream.parseSpecialWordValueAndCheckOneOf("T", "D", "TS");
        StringExpression dateValue = parseStringExpression();
        stream.checkIsCloseFigureParen(true);
        stream.moveCursor();
        if ("T".equalsIgnoreCase(dateTimeAlias)) {
            return new TimeExpression(dateTimeAlias, dateValue.getValue(), true);
        }
        if ("D".equalsIgnoreCase(dateTimeAlias)) {
            return new DateExpression(dateTimeAlias, dateValue.getValue(), true);
        }
        if ("TS".equalsIgnoreCase(dateTimeAlias)) {
            return new TimestampExpression(dateTimeAlias, dateValue.getValue(), true);
        }
        throw stream.createException("Не известное выражение типа даты в JDBC: '" + dateTimeAlias + "', value: '" + dateValue.getValue() + "'", from);
    }

    // 3.4
    private boolean checkIsConstExpression() {
        return stream.checkIsSpecialWordValueOneOf(SW_NULL, SW_TRUE, SW_FALSE);
    }

    // 3.4
    private ValueExpression parseConstExpression() {
        int from = stream.getCursor();
        String word = stream.parseSpecialWordValue();
        if (SW_NULL.equalsIgnoreCase(word)) {
            return new NullExpression(word);
        }
        if (SW_TRUE.equalsIgnoreCase(word)) {
            return new TrueExpression(word);
        }
        if (SW_FALSE.equalsIgnoreCase(word)) {
            return new FalseExpression(word);
        }
        throw stream.createException("Ожидается 'NULL' или 'TRUE' или 'FALSE', а получено '" + word + "'", from);
    }

    // 3.5
    private boolean checkIsQuestionExpression() {
        return stream.checkIsQuestionValue();
    }

    // 3.5
    private ValueExpression parseQuestionExpression() {
        stream.parseQuestionValueAndCheck();
        if (stream.getSymbol() == ':') {
            if ('O' == stream.getSymbol(1)) {
                stream.moveCursor(2);
                return new PageQuestionExpression(PQE_OFFSET);
            }
            if ('L' == stream.getSymbol(1)) {
                stream.moveCursor(2);
                return new PageQuestionExpression(PQE_LIMIT);
            }
        }
        return new QuestionExpression();
    }

    // 3.6
    private boolean checkIsFilterExpression() {
        return stream.checkIsFilterValue();
    }

    // 3.6 TODO улучшение: (вынести парсер FilterExpression)
    private FilterExpression parseFilterExpression() {
        int from = stream.getCursor();
        if (!checkIsFilterExpression()) {
            throw stream.createException("Ожидается символ ':', а получен '" + stream.getSymbol() + "' в выражении типа фильтр", from);
        }
        String filterValue = stream.parseFilterValue();
        int colonCount = filterValue.lastIndexOf(':') + 1;
        boolean questionFlag = filterValue.endsWith("?");
        String filterName = filterValue.substring(colonCount, questionFlag ? filterValue.length() - 1 : filterValue.length());
        FilterExpression filterExpr = new FilterExpression(filterValue);
        filterExpr.setColonCount(colonCount);
        filterExpr.setFilterName(filterName);
        filterExpr.setQuestionFlag(questionFlag);
        return filterExpr;
    }

    // 4
    private boolean checkIsCaseExpression() {
        return stream.checkIsSpecialWordValueSame(SW_CASE);
    }

    // 4
    private CaseExpression parseCaseExpression() {
        CaseExpression caseExpr = new CaseExpression();
        String caseWord = stream.parseSpecialWordValueAndCheck(SW_CASE);
        caseExpr.setCaseWord(caseWord);
        // 4.1
        boolean caseFlag = checkIsCaseOperandExpression();
        if (caseFlag) {
            // 4.1
            Expression operandExpr = parseCaseOperandExpression();
            caseExpr.setOperandExpr(operandExpr);
        }
        // 4.2
        while (checkIsWhenThenExpression()) {
            // 4.2
            WhenThenExpression whenThenExpr = parseWhenThenExpression(!caseFlag);
            caseExpr.addWhenThenExprs(whenThenExpr);
        }
        // 4.3
        if (checkIsElseExpression()) {
            // 4.3
            ElseExpression elseExpr = parseElseExpression();
            caseExpr.setElseExpr(elseExpr);
        }
        // 4.4
        String endWord = stream.parseSpecialWordValueAndCheck(SW_END);
        caseExpr.setEndWord(endWord);
        return caseExpr;
    }

    // 4.1
    private boolean checkIsCaseOperandExpression() {
        return !stream.checkIsSpecialWordValueSame(SW_WHEN);
    }

    // 4.1
    private Expression parseCaseOperandExpression() {
        return parseSingleSelectExpression();
    }

    // 4.2
    private boolean checkIsWhenThenExpression() {
        return stream.checkIsSpecialWordValueSame(SW_WHEN);
    }

    // 4.2
    private WhenThenExpression parseWhenThenExpression(boolean searchFlag) {
        String whenWord = stream.parseSpecialWordValueAndCheck(SW_WHEN);
        Expression whenExpr = searchFlag ? parseFullSearchConditionsExpr() : parseSingleSelectExpression();
        String thenWord = stream.parseSpecialWordValueAndCheck(SW_THEN);
        Expression thenExpr = parseSingleSelectExpression();
        WhenThenExpression whenThenExpr = new WhenThenExpression();
        whenThenExpr.setWhenWord(whenWord);
        whenThenExpr.setWhenExpr(whenExpr);
        whenThenExpr.setThenWord(thenWord);
        whenThenExpr.setThenExpr(thenExpr);
        return whenThenExpr;
    }

    // 4.3
    private boolean checkIsElseExpression() {
        return stream.checkIsSpecialWordValueSame(SW_ELSE);
    }

    // 4.3
    private ElseExpression parseElseExpression() {
        String elseWord = stream.parseSpecialWordValueAndCheck(SW_ELSE);
        Expression resultExpr = parseSingleSelectExpression();
        ElseExpression elseExpr = new ElseExpression();
        elseExpr.setElseWord(elseWord);
        elseExpr.setResultExpr(resultExpr);
        return elseExpr;
    }

    // 5
    private boolean checkIsCastExpression() {
        return stream.checkIsSpecialWordValueSame(SW_CAST);
    }

    // 5
    private CastExpression parseCastExpression() {
        String castWord = stream.parseSpecialWordValueAndCheck(SW_CAST);
        stream.checkIsOpenParent();
        stream.moveCursor();
        SelectableExpression operandExpr = parseSingleSelectExpression();
        String asWord = stream.parseSpecialWordValueAndCheck(SW_AS);
        FuncExpression targetExpr = parseFuncVariantExpression();
        stream.checkIsCloseParen();
        stream.moveCursor();
        CastExpression castExpr = new CastExpression();
        castExpr.setCastWord(castWord);
        castExpr.setOperandExpr(operandExpr);
        castExpr.setAsWord(asWord);
        castExpr.setTargetExpr(targetExpr);
        return castExpr;
    }

    // 6
    private boolean checkIsSubSelectQuery() {
        boolean subSelectFlag = stream.checkIsOpenParent(false);
        if (subSelectFlag) {
            stream.moveCursor();
            subSelectFlag = stream.checkIsSpecialWordValueSame(SW_SELECT);
            stream.moveCursor(-1);
        }
        return subSelectFlag;
    }

    // 6
    private SelectQuery parseSubSelectQuery() {
        stream.skipSpaces();
        stream.checkIsOpenParent();
        stream.moveCursor(); // skip '('
        SelectQuery subSelectQuery = parseSingleSelectQuery();
        stream.skipSpaces();
        stream.checkIsCloseParen();
        stream.moveCursor(); // skip ')'
        subSelectQuery.setWrapped(true);
        return subSelectQuery;
    }

    // 7
    private boolean checkIsColumnExpression() {
        return stream.checkIsIdentifierValue(true, true);
    }

    // 7
    /** TODO переписать -
     * добавить метод в stream, который пропускает точку
     * TODO несколько точек? I.AM.ColumnName
     * */
    private ColumnExpression parseColumnExpression() {
        String prefix = null;
        String columnName = null;
        int from = stream.getCursor();
        String value = stream.parseIdentifierValue(true);
        if (value == null) {
            throw stream.createException("Не является именем колонки", from);
        }
        if (stream.checkIsDot(false)) {
            prefix = value;
            stream.moveCursor();
            if (stream.checkIsAsteriskValue()) {
                stream.moveCursor();
                columnName = stream.getValueFrom(stream.getCursor() - 1);
            } else {
                from = stream.getCursor();
                columnName = stream.parseIdentifierValue(true);
                if (columnName == null) {
                    throw stream.createException("Не является именем колонки", from);
                }
            }
        } else {
            columnName = value;
        }
        ColumnExpression columnExpr = new ColumnExpression();
        columnExpr.setPrefix(prefix);
        columnExpr.setColumnName(columnName);
        return columnExpr;
    }

    private boolean checkIsTableReferenceExpression(boolean useJoin) {
        if (checkIsTableJoinExpression(useJoin)) {
            return true;
        }
        if (checkIsTableSubQueryExpression()) {
            return true;
        }
        if (checkIsTableValuesExpression()) {
            return true;
        }
        if (checkIsTableExpression()) {
            return true;
        }
        return false;
    }

    private TableReferenceExpression parseTableReferenceExpression(boolean checkTableExprOnJoin) {
        if (checkIsTableJoinExpression(checkTableExprOnJoin)) {
            return parseTableJoinExpression();
        }
        if (checkIsTableSubQueryExpression()) {
            return parseTableSubQueryExpression();
        }
        if (checkIsTableExpression()) {
            return parseTableExpression();
        }
        if (checkIsTableValuesExpression()) {
            return parseTableValuesExpression();
        }
        throw stream.createException("Неизвестный тип таблицы в секции FROM");
    }

    private boolean checkIsTableJoinExpression(boolean useJoin) {
        return useJoin && stream.checkIsSpecialWordValueOneOf(SW_INNER, SW_LEFT, SW_RIGHT, SW_FULL, SW_CROSS);
    }

    private TableJoinExpression parseTableJoinExpression() {
        TableJoinExpression tableJoinExpr = new TableJoinExpression();
        String joinWords = parseJoinWords();
        tableJoinExpr.setJoinWords(joinWords);
        TableJoinTypes joinType = resolveJoinType(joinWords);
        tableJoinExpr.setJoinType(joinType);
        tableJoinExpr.setTableRefExpr(parseTableReferenceExpression(false));
        if (stream.checkIsSpecialWordValueSame(SW_ON)) {
            String onWord = stream.parseSpecialWordValueAndCheck(SW_ON);
            Expression condExpr = parseOnConditionExpr();
            tableJoinExpr.setOnWord(onWord);
            tableJoinExpr.setOnConditionExpr(condExpr);
        }
        return tableJoinExpr;
    }

    private String parseJoinWords() {
        if (stream.checkIsSpecialWordSequents(OW_R_CROSS, OW_R_JOIN)) {
            return stream.parseSpecialWordSequents(OW_R_CROSS, OW_R_JOIN);
        }
        if (stream.checkIsSpecialWordSequents(OW_R_INNER, OW_R_JOIN)) {
            return stream.parseSpecialWordSequents(OW_R_INNER, OW_R_JOIN);
        }
        if (stream.checkIsSpecialWordSequents(OW_R_LEFT, OW_O_OUTER, OW_R_JOIN)) {
            return stream.parseSpecialWordSequents(OW_R_LEFT, OW_O_OUTER, OW_R_JOIN);
        }
        if (stream.checkIsSpecialWordSequents(OW_R_RIGHT, OW_O_OUTER, OW_R_JOIN)) {
            return stream.parseSpecialWordSequents(OW_R_RIGHT, OW_O_OUTER, OW_R_JOIN);
        }
        if (stream.checkIsSpecialWordSequents(OW_R_FULL, OW_O_OUTER, OW_R_JOIN)) {
            return stream.parseSpecialWordSequents(OW_R_FULL, OW_O_OUTER, OW_R_JOIN);
        }
        throw stream.createException("Некорректный синтаксис JOIN связки", stream.getCursor());
    }

    private TableJoinTypes resolveJoinType(String joinWords) {
        TableJoinTypes joinType = TableJoinTypes.resolveJoinType(joinWords);
        if (joinType == null) {
            throw stream.createException("Не удалось определить тип 'JOIN' соединения таблиц по параметру '" + joinWords + "'", stream.getCursor());
        }
        return joinType;
    }

    private boolean checkIsTableSubQueryExpression() {
        return checkIsSubSelectQuery();
    }

    private TableReferenceExpression parseTableSubQueryExpression() {
        SelectQuery selectQuery = parseSubSelectQuery();
        TableSubQueryExpression tableSubQuery = new TableSubQueryExpression();
        tableSubQuery.setSubQueryExpr(selectQuery);
        tableSubQuery.setAsClauseExpr(parseAsClauseExpression(false));
        return tableSubQuery;
    }

    private boolean checkIsTableExpression() {
        return stream.checkIsIdentifierValue(true, true);
    }

    private TableExpression parseTableExpression() {
        ColumnExpression columnExpr = parseColumnExpression();
        TableExpression tableExpr = new TableExpression();
        tableExpr.setSchemeName(columnExpr.getPrefix());
        tableExpr.setTableName(columnExpr.getColumnName());
        tableExpr.setAsClauseExpr(parseAsClauseExpression(false));
        return tableExpr;
    }

    private boolean checkIsTableValuesExpression() {
        stream.keepParserState();
        if (!stream.checkIsOpenParent(false)) {
            stream.rollbackParserState();
            return false;
        }
        stream.moveCursor();
        String word = stream.parseSpecialWordValue();
        stream.rollbackParserState();
        return SW_VALUES.equalsIgnoreCase(word);
    }

    private TableValuesExpression parseTableValuesExpression() {
        stream.checkIsOpenParent();
        stream.moveCursor();
        String valuesWord = stream.parseSpecialWordValueAndCheck(SW_VALUES);
        TableValuesExpression tableValuesExpr = new TableValuesExpression();
        tableValuesExpr.setValuesWord(valuesWord);
        ValueListExpression valueExpr = parseValueListExpression();
        tableValuesExpr.addValuesExpr(valueExpr);
        while (stream.checkIsComma()) {
            stream.moveCursor();
            valueExpr = parseValueListExpression();
            tableValuesExpr.addValuesExpr(valueExpr);
            stream.skipSpaces();
        }
        stream.checkIsCloseParen();
        stream.moveCursor();
        String asWord = stream.parseSpecialWordValueVariants(SW_AS);
        tableValuesExpr.setAsWord(asWord);
        FuncExpression templateExpr = parseFuncVariantExpression();
        tableValuesExpr.setTemplateExpr(templateExpr);
        return tableValuesExpr;
    }

    private Expression parseOnConditionExpr() {
        return parseFullSearchConditionsExpr();
    }

    private Expression parseFullSearchConditionsExpr() {
        return parseSearchConditionsExpr();
    }

    /** TODO: проработать вопрос со скобками */
    private Expression parseSearchConditionsExpr() {
        int from = stream.getCursor();
        boolean expressionState = true;
        ParenLevels levels = new ParenLevels();
        stream.skipSpaces();
        if (!isOpenBrace()) {
            levels.push(new ParenLevel(false));
        }
        while (true) {
            stream.skipSpaces();
            if (isOpenBrace()) {
                levels.incDepth();
                levels.push(new ParenLevel(true));
                stream.moveCursor();
                continue;
            }
            if (stream.checkIsCloseParen(false)) {
                levels.decDepth();
                if (!levels.isEmpty()) {
                    ParenLevel checkedLevel = levels.peek();
                    if (!checkedLevel.isUseParen()) {
                        levels.incDepth();
                        break;
                    }
                }
                if (levels.getDepth() < 0) {
                    throw stream.createException(
                            "Ошибка при разборе условий. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                stream.moveCursor();
                ParenLevel currentLevel = levels.pop();
                ParenLevel parentLevel = null;
                if (levels.isEmpty()) {
                    parentLevel = new ParenLevel(false);
                } else {
                    parentLevel = levels.pop();
                }
                parentLevel.add(currentLevel.combine(true));
                levels.push(parentLevel);
                continue;
            }
            if (expressionState) {
                from = stream.getCursor();
                if (checkIsNotWord()) {
                    NotExpression notExpr = parseNotExpr();
                    ParenLevel currentLevel = levels.peek();
                    currentLevel.add(notExpr);
                    continue;
                }
                from = stream.getCursor();
                Expression expr = parseSearchConditionExpr(levels);
                ParenLevel currentLevel = levels.peek();
                if (currentLevel == null) {
                    currentLevel = new ParenLevel(false);
                    levels.push(currentLevel);
                    // throw stream.createException("Ошибка при разборе условий", from);
                }
                currentLevel.add(expr);
                expressionState = false;
                continue;
            }
            from = stream.getCursor();
            ConditionFlowType condType = parseConditionFlowType();
            if (condType == null) {
                break;
            }
            ParenLevel currentLevel = levels.peek();
            currentLevel.add(condType);
            expressionState = true;
        }
        if (levels.getDepth() != 0) {
            throw stream.createException(
                    "Ошибка при разборе условий. Количество закрывающих скобок ')' меньше, чем открывающихся '('. depth = "
                            + levels.getDepth(),
                    from);
        }
        if (levels.size() != 1) {
            throw stream.createException("Ошибка при разборе условий. Количество вложений '" + levels.size() + "'", from);
        }
        ParenLevel rootLevel = levels.pop();
        Expression searchExprs = rootLevel.combine(false);
        return searchExprs;
    }

    private boolean isOpenBrace() {
        return stream.checkIsOpenParent(false) && !checkIsSubSelectQuery();
    }

    private Expression parseSearchConditionExpr(ParenLevels levels) {
        if (checkIsRowValueConstructorExpression()) {
            Expression searchExpr = parseSingleRowValueConstructorExpr(levels);
            if (checkIsQuantifiedComparisonPredicateExpression()) {
                QuantifiedComparisonPredicateExpression quanifiedComparisonPredicateExpr = completeQuantifiedComparisonPredicateExpression(searchExpr);
                return quanifiedComparisonPredicateExpr;
            }
            if (checkIsComparisonOperator()) {
                Expression compPredicateExpr = completeComparisonPredicateExpr(searchExpr);
                return compPredicateExpr;
            }
            if (checkIsNullablePredicateExpression()) {
                NullablePredicateExpression nullPredicateExpr = completeNullablePredicateExpression(searchExpr);
                return nullPredicateExpr;
            }
            if (checkIsInPredicateExpression()) {
                InPredicateExpression inPredicateExpr = completeInPredicateExpression(searchExpr);
                return inPredicateExpr;
            }
            if (checkIsLikePredicateExpression()) {
                LikePredicateExpression likePredicateExpr = completeLikePredicateExpression(searchExpr);
                return likePredicateExpr;
            }
            if (checkIsBetweenPredicateExpression()) {
                BetweenPredicateExpression betweenPredicateExpr = completeBetweenPredicateExpression(searchExpr);
                return betweenPredicateExpr;
            }
            if (checkIsValuesComparisonPredicateExpression()) {
                ComparisonPredicateExpression valueCompPredicateExpr = completeValuesComparisonPredicateExpression(searchExpr, levels);
                return valueCompPredicateExpr;
            }
            // Примечание: считаем что это одиночное выражение boolean типа
            return searchExpr;
        }
        if (checkIsExistsPredicateExpression()) {
            ExistsPredicateExpression existsPredicate = parseExistsPredicateExpr();
            return existsPredicate;
        }
        if (checkIsUniquePredicateExpression()) {
            UniquePredicateExpression uniquePredicate = parseUniquePredicateExpression();
            return uniquePredicate;
        }
        throw stream.createException("Неизвестное выражение для условия поиска", stream.getCursor());
    }

    private Expression parseFullSingleRowValueConstructorExpr() {
        ParenLevels levels = new ParenLevels();
        if (!isOpenBrace()) {
            levels.push(new ParenLevel(false));
        }
        Expression expr = parseSingleRowValueConstructorExpr(levels);
        return expr;
    }

    /*** TODO переписать */
    private Expression parseSingleRowValueConstructorExpr(ParenLevels levels) {
        int from = stream.getCursor();
        boolean expressionState = true;
        int fromIndex = levels.isEmpty() ? 0 : levels.peek().size();
        stream.skipSpaces();
        while (true) {
            stream.skipSpaces();
            if (isOpenBrace()) {
                levels.incDepth();
                levels.push(new ParenLevel(true));
                stream.moveCursor();
                continue;
            }
            if (stream.checkIsCloseParen(false)) {
                levels.decDepth();
                if (!levels.isEmpty()) {
                    ParenLevel checkedLevel = levels.peek();
                    if (!checkedLevel.isUseParen()) {
                        levels.decDepth();
                        break;
                    }
                }
                if (levels.getDepth() < 0) {
                    throw stream.createException(
                            "Ошибка при разборе выборки. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                stream.moveCursor();
                ParenLevel currentLevel = levels.pop();
                ParenLevel parentLevel = null;
                if (levels.isEmpty()) {
                    parentLevel = new ParenLevel(false);
                } else {
                    parentLevel = levels.pop();
                }
                fromIndex = parentLevel.size();
                Expression layerExpression = currentLevel.combine(true);
                ExtendsSelectableExpression extSelectableExpr = parseExtendsSelectableExpression();
                if (extSelectableExpr != null) {
                    extSelectableExpr.setPrimaryExpr(layerExpression);
                    layerExpression = extSelectableExpr;
                }
                parentLevel.add(layerExpression);
                levels.push(parentLevel);
                continue;
            }
            if (expressionState) {
                Expression expr = parseRowValueConstructorExpr();
                ParenLevel currentLevel = levels.peek();
                currentLevel.add(expr);
                expressionState = false;
                continue;
            }
            if (!checkIsMathOperator()) {
                break;
            }
            MathOperator mathOperator = parseMathOperator();
            ParenLevel currentLevel = levels.peek();
            currentLevel.add(mathOperator);
            expressionState = true;
        }
        ParenLevel currentLevel = levels.peek();
        Expression searchExprs = currentLevel.combineFrom(false, fromIndex);
        return searchExprs;
    }

    private NotExpression parseNotExpr() {
        String notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        NotExpression notExpr = new NotExpression();
        notExpr.setNotWord(notWord);
        return notExpr;
    }

    private Expression parseRowValueConstructorExpr() {
        if (checkIsValueExpression()) {
            return parseValueExpression();
        }
        if (checkIsFuncExpression()) {
            return parseFuncExpression();
        }
        if (checkIsCaseExpression()) {
            return parseCaseExpression();
        }
        if (checkIsSubSelectQuery()) {
            return parseSubSelectQuery();
        }
        if (checkIsColumnExpression()) {
            return parseColumnExpression();
        }
        if (checkIsCaseExpression()) {
            return parseCaseExpression();
        }
        throw stream.createException("Неизвестное выражение для условия поиска <row value constructor>", stream.getCursor());
    }

    private ExistsPredicateExpression parseExistsPredicateExpr() {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String existsWord = stream.parseSpecialWordValueAndCheck(SW_EXISTS);
        if (!checkIsSubSelectQuery()) {
            throw stream.createException("Ожидается подзапрос в 'EXISTS' выражении", stream.getCursor());
        }
        SelectQuery subSelectExpr = parseSubSelectQuery();
        ExistsPredicateExpression existsExpr = new ExistsPredicateExpression();
        existsExpr.setNotWord(notWord);
        existsExpr.setExistsWord(existsWord);
        existsExpr.setSelectQuery(subSelectExpr);
        return existsExpr;
    }

    private UniquePredicateExpression parseUniquePredicateExpression() {
        String uniqueWord = stream.parseSpecialWordValueAndCheck(SW_UNIQUE);
        SelectQuery subSelectExpr = parseSubSelectQuery();
        UniquePredicateExpression uniquePredicateExpr = new UniquePredicateExpression();
        uniquePredicateExpr.setUniqueWord(uniqueWord);
        uniquePredicateExpr.setSubQuery(subSelectExpr);
        return uniquePredicateExpr;
    }

    private QuantifiedComparisonPredicateExpression completeQuantifiedComparisonPredicateExpression(Expression expression) {
        ComparisonOperator comparisonOperationType = parseComparisonOperatorType();
        String quantifierWord = stream.parseSpecialWordValueAndCheckOneOf(SW_ANY, SW_SOME, SW_ALL);
        SelectQuery subSelectQuery = parseSubSelectQuery();
        QuantifiedComparisonPredicateExpression quanCompPredicateExpr = new QuantifiedComparisonPredicateExpression();
        quanCompPredicateExpr.setBaseExpr(expression);
        quanCompPredicateExpr.setCompOperatorType(comparisonOperationType);
        quanCompPredicateExpr.setQuantifierWord(quantifierWord);
        quanCompPredicateExpr.setSubSelectQuery(subSelectQuery);
        return quanCompPredicateExpr;
    }

    private ComparisonOperator parseComparisonOperatorType() {
        int from = stream.getCursor();
        String operatorValue = stream.parseComparisonOperatorValue();
        ComparisonOperator operator = resolveComparisonOperatorType(operatorValue);
        if (operator == null) {
            throw stream.createException("Ожидается операция сравнения '=' или '<' или '<=' или '>' или '>=' или '<>' или '!='", from);
        }
        return operator;
    }

    private NullablePredicateExpression completeNullablePredicateExpression(Expression expression) {
        NullablePredicateExpression nullPredicateExpr = new NullablePredicateExpression();
        nullPredicateExpr.setExpression(expression);
        String isWord = stream.parseSpecialWordValueAndCheck(SW_IS);
        nullPredicateExpr.setIsWord(isWord);
        if (checkIsNotWord()) {
            String notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
            nullPredicateExpr.setNotWord(notWord);
        }
        String nullWord = stream.parseSpecialWordValueAndCheck(SW_NULL);
        nullPredicateExpr.setNullWord(nullWord);
        return nullPredicateExpr;
    }

    private InPredicateExpression completeInPredicateExpression(Expression baseExpression) {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String inWord = stream.parseSpecialWordValueAndCheck(SW_IN);
        Expression predicateValueExpr = parsePredicateValueExpression();
        InPredicateExpression inPredicateExpr = new InPredicateExpression();
        inPredicateExpr.setNotWord(notWord);
        inPredicateExpr.setInWord(inWord);
        inPredicateExpr.setBaseExpression(baseExpression);
        inPredicateExpr.setPredicateValueExpr(predicateValueExpr);
        return inPredicateExpr;
    }

    private Expression parsePredicateValueExpression() {
        if (checkIsSubSelectQuery()) {
            return parseSubSelectQuery();
        }
        return parseValueListExpression();
    }

    private LikePredicateExpression completeLikePredicateExpression(Expression baseExpression) {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String likeWord = stream.parseSpecialWordValueAndCheck(SW_LIKE);
        SelectableExpression patternExpr = parseSingleSelectExpression();
        EscapeExpression escapeExpr = null;
        if (checkIsEscapeExpression()) {
            escapeExpr = parseEscapeExpression();
        }
        LikePredicateExpression likeExpr = new LikePredicateExpression();
        likeExpr.setMatchValueExpr(baseExpression);
        likeExpr.setNotWord(notWord);
        likeExpr.setLikeWord(likeWord);
        likeExpr.setPatternExpr(patternExpr);
        likeExpr.setEscapeExpr(escapeExpr);
        return likeExpr;
    }

    private EscapeExpression parseEscapeExpression() {
        String escapeWord = stream.parseSpecialWordValueAndCheck(SW_ESCAPE);
        Expression escapeValueExpr = parseSingleSelectExpression();
        EscapeExpression escapeExpr = new EscapeExpression();
        escapeExpr.setEscapeWord(escapeWord);
        escapeExpr.setEscapeValueExpr(escapeValueExpr);
        return escapeExpr;
    }

    private BetweenPredicateExpression completeBetweenPredicateExpression(Expression baseExpression) {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String betweenWord = stream.parseSpecialWordValueAndCheck(SW_BETWEEN);
        Expression leftExpr = parseFullSingleRowValueConstructorExpr();
        String andWord = stream.parseSpecialWordValueAndCheck(SW_AND);
        Expression rightExpr = parseFullSingleRowValueConstructorExpr();
        BetweenPredicateExpression betweenPredicateExpr = new BetweenPredicateExpression();
        betweenPredicateExpr.setBaseExpression(baseExpression);
        betweenPredicateExpr.setNotWord(notWord);
        betweenPredicateExpr.setBetweenWord(betweenWord);
        betweenPredicateExpr.setLeftExpression(leftExpr);
        betweenPredicateExpr.setAndWord(andWord);
        betweenPredicateExpr.setRightExpression(rightExpr);
        return betweenPredicateExpr;
    }

    private ComparisonPredicateExpression completeComparisonPredicateExpr(Expression leftExpr) {
        ComparisonPredicateExpression cmpExpr = new ComparisonPredicateExpression();
        cmpExpr.setLeftExpr(leftExpr);
        ComparisonOperator operType = parseComparisonOperatorType();
        cmpExpr.setCompOperatorType(operType);
        Expression rightExpr = parseFullSingleRowValueConstructorExpr();
        cmpExpr.setRightExpr(rightExpr);
        return cmpExpr;
    }

    private ConditionFlowType parseConditionFlowType() {
        String conditionWord = stream.parseSpecialWordValueVariants(SW_AND, SW_OR);
        ConditionFlowType condType = resolveConditionFlowType(conditionWord);
        return condType;
    }

    private ComparisonPredicateExpression completeValuesComparisonPredicateExpression(Expression firstExpr, ParenLevels levels) {
        int from = stream.getCursor();
        if (levels.isEmpty()) {
            throw stream.createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        ValueListExpression leftValuesExpr = parseValueListExpressionFromSecondExpression(true, firstExpr);
        ParenLevel level = levels.pop();
        if (!level.isEmpty()) {
            throw stream.createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        levels.decDepth();
        ComparisonOperator operType = parseComparisonOperatorType();
        ValueListExpression rightExpr = parseValueListExpression();
        ComparisonPredicateExpression compPredicateExpr = new ComparisonPredicateExpression();
        compPredicateExpr.setLeftExpr(leftValuesExpr);
        compPredicateExpr.setCompOperatorType(operType);
        compPredicateExpr.setRightExpr(rightExpr);
        return compPredicateExpr;
    }

    private GroupingColumnReferenceExpression parseGroupingColumnReferenceExpression() {
        ColumnExpression columnExpr = parseColumnExpression();
        GroupingColumnReferenceExpression refColumnExpr = new GroupingColumnReferenceExpression();
        refColumnExpr.setColumnRefExpr(columnExpr);
        return refColumnExpr;
    }

    private List<SortKeyExpression> parseSortKeyExpressions() {
        List<SortKeyExpression> sortKeyExprs = new ArrayList<>();
        SortKeyExpression firstSortKeyExpr = parseSortKeyExpression();
        sortKeyExprs.add(firstSortKeyExpr);
        while (stream.checkIsComma()) {
            stream.moveCursor();
            SortKeyExpression nextSortKeyExpr = parseSortKeyExpression();
            sortKeyExprs.add(nextSortKeyExpr);
        }
        return sortKeyExprs;
    }

    private SortKeyExpression parseSortKeyExpression() {
        int from = stream.getCursor();
        Expression sortKeyValueExpr = parseSimpleArgExpression();
        if (sortKeyValueExpr == null) {
            throw stream.createException("Ожидается значение сортировки в секции 'ORDER BY'", from);
        }
        SortKeyExpression sortKeyExpr = new SortKeyExpression();
        sortKeyExpr.setSortKeyValueExpr(sortKeyValueExpr);
        if (checkIsCollateExpression()) {
            CollateExpression collateExpr = parseCollateExpression();
            sortKeyExpr.setCollateExpr(collateExpr);
        }
        if (checkIsOrderingSpecificationWord()) {
            String orderingSpec = parseOrderingSpecification();
            sortKeyExpr.setOrderingSpec(orderingSpec);
        }
        return sortKeyExpr;
    }

    private CollateExpression parseCollateExpression() {
        String collateWord = stream.parseSpecialWordValueAndCheck(SW_COLLATE);
        Expression collationNameExpr = parseColumnExpression();
        CollateExpression collateExpr = new CollateExpression();
        collateExpr.setCollateWord(collateWord);
        collateExpr.setCollationNameExpr(collationNameExpr);
        return collateExpr;
    }

    private String parseOrderingSpecification() {
        return stream.parseSpecialWordValueAndCheckOneOf(SW_ASC, SW_DESC);
    }

    private LimitExpression parseLimitExpression() {
        if (!checkIsLimitExpression()) {
            return null;
        }
        String limitWord = stream.parseSpecialWordValueAndCheck(SW_LIMIT);
        LimitExpression limitExpr = new LimitExpression();
        limitExpr.setLimitWord(limitWord);
        int from = stream.getCursor();
        if (checkIsValueExpression()) {
            ValueExpression valueExpr = parseValueExpression();
            limitExpr.setLimitExpr(valueExpr);
        } else if (checkIsAllWord()) {
            String allWord = stream.parseSpecialWordValueAndCheck(SW_ALL);
            KeyWordExpression kwAllExpr = new KeyWordExpression(allWord);
            limitExpr.setLimitExpr(kwAllExpr);
        } else {
            throw stream.createException("Ожидается служебное слово 'ALL' или выражение значения в секции 'LIMIT'", from);
        }
        return limitExpr;
    }

    private OffsetExpression parseOffsetExpression() {
        if (!checkIsOffsetExpression()) {
            return null;
        }
        String offsetWord = stream.parseSpecialWordValueAndCheck(SW_OFFSET);
        OffsetExpression offsetExpr = new OffsetExpression();
        offsetExpr.setOffsetWord(offsetWord);
        int from = stream.getCursor();
        if (checkIsValueExpression()) {
            ValueExpression valueExpr = parseValueExpression();
            offsetExpr.setOffsetExpr(valueExpr);
        } else {
            throw stream.createException("Ожидается выражение значения в секции 'OFFSET'", from);
        }
        String rowsWord = stream.parseSpecialWordValueVariants(SW_ROWS, SW_ROW);
        offsetExpr.setRowsWord(rowsWord);
        return offsetExpr;
    }

    private MathOperator parseMathOperator() {
        int from = stream.getCursor();
        String mathOperatorValue = stream.parseMathOperatorValue();
        MathOperator mathOperator = resolveMathOperator(mathOperatorValue);
        if (mathOperator == null) {
            throw stream.createException("Ожидается математический оператор", from);
        }
        return mathOperator;
    }

    /** @return ALL или DISTINCT или null */
    private String parseSelectModeWord() {
        return stream.parseSpecialWordValueVariants(SW_ALL, SW_DISTINCT);
    }

    private String parseAllWord() {
        return stream.parseSpecialWordValueAndCheck(SW_ALL);
    }

    private String parseLinkedWord() {
        return stream.parseSpecialWordValueAndCheckOneOf(SW_UNION, SW_EXCEPT, SW_INTERSECT, SW_MINUS);
    }

    // ----------------------------------------------------------- WORD ----------------------------------------------------------------------------- \\
    private boolean checkIsAllWord() {
        return stream.checkIsSpecialWordValueSame(SW_ALL);
    }

    private boolean checkIsNotWord() {
        return stream.checkIsSpecialWordValueSame(SW_NOT);
    }

    private boolean checkIsOrderingSpecificationWord() {
        return stream.checkIsSpecialWordValueOneOf(SW_ASC, SW_DESC);
    }

    private boolean checkIsLinkedWord() {
        return stream.checkIsSpecialWordValueOneOf(SW_UNION, SW_EXCEPT, SW_INTERSECT, SW_MINUS);
    }

    private boolean checkIsEscapeExpression() {
        return stream.checkIsSpecialWordValueSame(SW_ESCAPE);
    }

    private boolean checkIsCollateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_COLLATE);
    }

    private boolean checkIsNullablePredicateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_IS);
    }

    private boolean checkIsLimitExpression() {
        return stream.checkIsSpecialWordValueSame(SW_LIMIT);
    }

    private boolean checkIsOffsetExpression() {
        return stream.checkIsSpecialWordValueSame(SW_OFFSET);
    }

    private boolean checkIsUniquePredicateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_UNIQUE);
    }

    private boolean checkIsWhereStatement() {
        return stream.checkIsSpecialWordValueSame(SW_WHERE);
    }

    private boolean checkIsHavingStatement() {
        return stream.checkIsSpecialWordValueSame(SW_HAVING);
    }

    // ------------------------------------------------------ WORD SEQUENSE ----------------------------------------------------------------------------- \\
    private boolean checkIsExistsPredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_EXISTS);
    }

    private boolean checkIsInPredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_IN);
    }

    private boolean checkIsLikePredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_LIKE);
    }

    private boolean checkIsBetweenPredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_BETWEEN);
    }

    private boolean checkIsGroupByStatement() {
        return stream.checkIsSpecialWordSequents(OW_R_GROUP, OW_R_BY);
    }

    private boolean checkIsOrderByStatement() {
        return stream.checkIsSpecialWordSequents(OW_R_ORDER, OW_R_BY);
    }

    private boolean checkIsFetchStatement() {
        return stream.checkIsSpecialWordValueSame(SW_FETCH);
    }

    private boolean checkIsPartitionByStatement() {
        return stream.checkIsSpecialWordSequents(OW_R_PARTITION, OW_R_BY);
    }

    // -------------------------------------------------------------- SIMPLE ----------------------------------------------------------------------------- \\
    private boolean checkIsValuesComparisonPredicateExpression() {
        return stream.checkIsComma();
    }

    // ----------------------------------------------------------- OPERATOR ----------------------------------------------------------------------------- \\
    private boolean checkIsMathOperator() {
        return stream.checkIsMathOperatorValue();
    }

    private boolean checkIsComparisonOperator() {
        return stream.checkIsComparisonOperatorValue();
    }

    // ----------------------------------------------------------- COMPOSITE ----------------------------------------------------------------------------- \\
    private boolean checkIsRowValueConstructorExpression() {
        if (checkIsValueExpression()) {
            return true;
        }
        if (checkIsFuncExpression()) {
            return true;
        }
        if (checkIsCaseExpression()) {
            return true;
        }
        if (checkIsSubSelectQuery()) {
            return true;
        }
        if (checkIsColumnExpression()) {
            return true;
        }
        return false;
    }

    // ----------------------------------------------------------- LOGIC ----------------------------------------------------------------------------- \\
    private boolean checkIsQuantifiedComparisonPredicateExpression() {
        if (!checkIsComparisonOperator()) {
            return false;
        }
        stream.keepParserState();
        parseComparisonOperatorType();
        String word = stream.parseSpecialWordValue();
        stream.rollbackParserState();
        return SW_ANY.equalsIgnoreCase(word) || SW_SOME.equalsIgnoreCase(word) || SW_ALL.equalsIgnoreCase(word);
    }
}
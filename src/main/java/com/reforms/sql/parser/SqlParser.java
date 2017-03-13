package com.reforms.sql.parser;

import com.reforms.sql.expr.query.LinkingSelectQuery;
import com.reforms.sql.expr.query.SelectQuery;
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
import static com.reforms.sql.expr.term.from.TableJoinTypes.TJT_CROSS_JOIN;
import static com.reforms.sql.expr.term.predicate.ComparisonOperator.resolveComparisonOperatorType;
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
 *      - query         (SelectQuery, InsertQuery, UpdateQuery, DeleteQuery)
 *      - statement     (SelectStatement, FromStatement, WhereStatement, ... etc)
 *      - expression    (AliasExpression, AsteriskExpression, ... etc)
 *      - word          (ALL, NOT, ... etc, part of some expression, but exclude main word)
 *      - operator      (MathOperator, ComparisonOperator)
 *
 * TODO времено код разбит на смысловые части не связанные между собой. Необходимо перегруппировать участки кода так, чтобы было видно общий смысл
 */
public class SqlParser {

    private AbstractSqlStream stream;

    public SqlParser(String query) {
        stream = new SqlStream(query);
    }

    public SelectQuery parseSelectQuery() {
        SelectQuery directSelectQuery = parseSingleSelectQuery();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать запрос");
        }
        return directSelectQuery;
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
        // 3. SELECT STATEMENT
        SelectStatement selectStatement = new SelectStatement();
        SelectableExpression selectExpr = parseFullSelectableExpression();
        if (selectExpr == null) {
            throw stream.createException("В выражении SELECT должен быть хотя бы 1 параметр выборки", stream.getCursor());
        }
        while (selectExpr != null) {
            selectStatement.addSelectExpression(selectExpr);
            if (!stream.checkIsFuncArgsDelim()) {
                break;
            }
            char symbol = stream.parseFuncArgsDelim();
            if (')' == symbol) {
                break;
            }
            stream.moveCursor();
            selectExpr = parseFullSelectableExpression();
        }
        selectStatement.setSelectWord(selectWord);
        selectStatement.setModeWord(selectModeWord);
        return selectStatement;
    }

    private FromStatement parseFromStatement() {
        if (stream.checkIsSpecialWordValue()) {
            String fromWord = stream.parseSpecialWordValueAndCheck(SW_FROM);
            FromStatement fromStatement = new FromStatement();
            fromStatement.setFromWord(fromWord);
            List<TableReferenceExpression> tableRefExprs = fromStatement.getTableRefExprs();
            while (checkIsTableReferenceExpression(!tableRefExprs.isEmpty())) {
                TableReferenceExpression tableRefExpr = parseTableReference(!tableRefExprs.isEmpty());
                fromStatement.addTableRefExpr(tableRefExpr);
                if (',' == stream.getSymbol()) {
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
                throw stream.createException("Ожидается в секции 'FROM' блок данных о таблицах", stream.getCursor());
            }
            return fromStatement;
        }
        return null;
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
            stream.skipSpaces();
            while (',' == stream.getSymbol()) {
                stream.moveCursor();
                GroupingColumnReferenceExpression refColumnExpr = parseGroupingColumnReferenceExpression();
                groupByStatement.addGroupByExpr(refColumnExpr);
                stream.skipSpaces();
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
            SelectQuery linkedSelectQuery = parseSingleSelectQuery();
            linkedSelectExpr.setLinkedSelectQuery(linkedSelectQuery);
            linkingSelectQueries.add(linkedSelectExpr);
        }
        return linkingSelectQueries;
    }

    private OrderByStatement parseOrderByStatement() {
        if (checkIsOrderByStatement()) {
            String orderWord = stream.parseSpecialWordValueAndCheck(SW_ORDER);
            String byWord = stream.parseSpecialWordValueAndCheck(SW_BY);
            OrderByStatement orderByStatement = new OrderByStatement();
            orderByStatement.setOrderWord(orderWord);
            orderByStatement.setByWord(byWord);
            SortKeyExpression firstSortKeyExpr = parseSortKeyExpression();
            orderByStatement.addSortKeyExpr(firstSortKeyExpr);
            while (',' == stream.getSymbol()) {
                stream.moveCursor();
                SortKeyExpression nextSortKeyExpr = parseSortKeyExpression();
                orderByStatement.addSortKeyExpr(nextSortKeyExpr);
            }
            return orderByStatement;
        }
        return null;
    }

    private PageStatement parsePageStatement() {
        LimitExpression limitExpr = parseLimitExpression();
        OffsetExpression offsetExpr = parseOffsetExpression();
        if (offsetExpr != null && limitExpr == null) {
            limitExpr = parseLimitExpression();
        }
        if (limitExpr == null && offsetExpr == null) {
            return null;
        }
        PageStatement pageStatement = new PageStatement();
        pageStatement.setLimitExpr(limitExpr);
        pageStatement.setOffsetExpr(offsetExpr);
        return pageStatement;
    }

    /** @return ALL или DISTINCT или null */
    private String parseSelectModeWord() {
        return stream.parseSpecialWordValueVariants(SW_ALL, SW_DISTINCT);
    }

    private SelectableExpression parseFullSelectableExpression() {
        stream.skipSpaces();
        SelectableExpression selectExpr = parseSingleSelectExpr();
        if (selectExpr != null) {
            AsClauseExpression asClauseExpr = parseAsClauseExpression(true);
            if (asClauseExpr != null) {
                AliasExpression aliasExpr = new AliasExpression();
                aliasExpr.setPrimaryExpr(selectExpr);
                aliasExpr.setAsClauseExpr(asClauseExpr);
                selectExpr = aliasExpr;
            }
        }
        return selectExpr;
    }

    private SelectableExpression parseSingleSelectExpr() {
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
            if (isCloseBrace()) {
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
                parentLevel.add(currentLevel.combine(true));
                levels.push(parentLevel);
                continue;
            }
            if (expressionState) {
                Expression expr = parseSelectableExpression();
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

    private MathOperator parseMathOperator() {
        int from = stream.getCursor();
        String mathOperatorValue = stream.parseMathOperatorValue();
        MathOperator mathOperator = resolveMathOperator(mathOperatorValue);
        if (mathOperator == null) {
            throw stream.createException("Ожидается математический оператор", from);
        }
        return mathOperator;
    }

    private SelectableExpression parseSelectableExpression() {
        stream.skipSpaces();
        if (checkIsAsteriskExpression()) {
            return parseAsteriskExpr();
        }
        if (checkIsFuncExpression()) {
            return parseFuncExpr();
        }
        if (checkIsValueExpression()) {
            return parseValueExpr();
        }
        if (checkIsCaseExpression()) {
            return parseCaseExpr();
        }
        if (checkIsCastExpression()) {
            return parseCastExpr();
        }
        if (checkIsSubSelectQuery()) {
            return parseSubSelect();
        }
        if (checkIsColumnExpression()) {
            return parseColumnExpr();
        }
        throw stream.createException("Неизвестное выражение для выборки", stream.getCursor());
    }

    private AsteriskExpression parseAsteriskExpr() {
        if (!checkIsAsteriskExpression()) {
            throw stream.createException("Ожидается символ '*'");
        }
        stream.moveCursor();
        return new AsteriskExpression();
    }

    private FuncExpression parseFuncExpr() {
        FuncExpression funcExpr = new FuncExpression();
        int from = stream.getCursor();
        String funcName = stream.parseSpecialWordValue(false);
        if (funcName == null) {
            throw stream.createException("Ожидается наименование функции", from);
        }
        funcExpr.setName(funcName);
        stream.skipSpaces();
        from = stream.getCursor();
        char symol = stream.getSymbol();
        if ('(' != symol) {
            throw stream.createException("Ожидается начало функции '(', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        String quantifier = parseSelectModeWord();
        funcExpr.setQuantifier(quantifier);
        from = stream.getCursor();
        stream.skipSpaces();
        symol = stream.getSymbol();
        if (')' != symol) {
            from = stream.getCursor();
            SelectableExpression arg = parseSingleSelectExpr();
            while (true) {
                funcExpr.addArg(arg);
                char symbol = stream.parseFuncArgsDelim();
                if (')' == symbol) {
                    break;
                }
                if (',' == symbol) {
                    stream.moveCursor();
                    from = stream.getCursor();
                    arg = parseSingleSelectExpr();
                } else {
                    throw stream.createException("Ожидается разделитель между аргументами функции ',', а получен '" + symbol + "'", from);
                }
            }
            stream.skipSpaces();
        }
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается заврешение функции символом ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        return funcExpr;
    }

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

    private NumericExpression parseNumericExpr() {
        int from = stream.getCursor();
        String numericValue = stream.parseNumericValue();
        if (numericValue == null) {
            throw stream.createException("Ожидается числовое значение", from);
        }
        return new NumericExpression(numericValue);
    }

    private StringExpression parseStringExpr() {
        int from = stream.getCursor();
        String stringValue = stream.parseStringValue();
        if (stringValue == null) {
            throw stream.createException("Ожидается строковое значение", from);
        }
        return new StringExpression(stringValue);
    }

    private ValueExpression parseDateExpr() {
        int from = stream.getCursor();
        String dateWord = stream.parseSpecialWordValueAndCheckOneOf(SW_TIME, SW_DATE, SW_TIMESTAMP, SW_INTERVAL);
        StringExpression dateValue = parseStringExpr();
        if (SW_TIME.equalsIgnoreCase(dateWord)) {
            return new TimeExpression(dateWord, dateValue.getValue());
        }
        if (SW_DATE.equalsIgnoreCase(dateWord)) {
            return new DateExpression(dateWord, dateValue.getValue());
        }
        if (SW_TIMESTAMP.equalsIgnoreCase(dateWord)) {
            return new TimestampExpression(dateWord, dateValue.getValue());
        }
        if (SW_INTERVAL.equalsIgnoreCase(dateWord)) {
            return new IntervalExpression(dateWord, dateValue.getValue());
        }
        throw stream.createException("Не известное выражение типа даты: '" + dateWord + "', value: '" + dateValue.getValue() + "'", from);
    }

    private ValueExpression parseConstExpr() {
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

    private QuestionExpression parseQuestionExpr() {
        if (!checkIsQuestionExpression()) {
            throw stream.createException("Ожидается '?', а получен символ '" + stream.getSymbol() + "'");
        }
        stream.moveCursor();
        return new QuestionExpression();
    }

    private ValueExpression parseValueExpr() {
        if (checkIsNumericExpression()) {
            return parseNumericExpr();
        }
        if (checkIsStringExpression()) {
            return parseStringExpr();
        }
        if (checkIsDateExpression()) {
            return parseDateExpr();
        }
        if (checkIsConstExpression()) {
            return parseConstExpr();
        }
        if (checkIsQuestionExpression()) {
            return parseQuestionExpr();
        }
        if (checkIsFilterExpression()) {
            return parseFilterExpr();
        }
        return null;
    }

    private CaseExpression parseCaseExpr() {
        CaseExpression caseExpr = new CaseExpression();
        String caseWord = stream.parseSpecialWordValueAndCheck(SW_CASE);
        caseExpr.setCaseWord(caseWord);
        boolean searchFlag = checkIsWhenThenExpression();
        if (!searchFlag) {
            Expression operandExpr = parseCaseOperandExpr();
            caseExpr.setOperandExpr(operandExpr);
        }
        while (checkIsWhenThenExpression()) {
            WhenThenExpression whenThenExpr = parseWhenThenExpr(searchFlag);
            caseExpr.addWhenThenExprs(whenThenExpr);
        }
        // TODO: добавить ElseExpression
        if (checkIsElseExpression()) {
            ElseExpression elseExpr = parseElseExpr();
            caseExpr.setElseExpr(elseExpr);
        }
        String endWord = stream.parseSpecialWordValueAndCheck(SW_END);
        caseExpr.setEndWord(endWord);
        return caseExpr;
    }

    /**
     * TODO: переделать через parseStatementWord(false, FILTER_CHARS);
     * @return
     */
    private FilterExpression parseFilterExpr() {
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

    private Expression parseCaseOperandExpr() {
        return parseSingleSelectExpr();
    }

    private WhenThenExpression parseWhenThenExpr(boolean searchFlag) {
        String whenWord = stream.parseSpecialWordValueAndCheck(SW_WHEN);
        Expression whenExpr = searchFlag ? parseFullSearchConditionsExpr() : parseSingleSelectExpr();
        String thenWord = stream.parseSpecialWordValueAndCheck(SW_THEN);
        Expression thenExpr = parseSingleSelectExpr();
        WhenThenExpression whenThenExpr = new WhenThenExpression();
        whenThenExpr.setWhenWord(whenWord);
        whenThenExpr.setWhenExpr(whenExpr);
        whenThenExpr.setThenWord(thenWord);
        whenThenExpr.setThenExpr(thenExpr);
        return whenThenExpr;
    }

    private ElseExpression parseElseExpr() {
        String elseWord = stream.parseSpecialWordValueAndCheck(SW_ELSE);
        Expression resultExpr = parseSingleSelectExpr();
        ElseExpression elseExpr = new ElseExpression();
        elseExpr.setElseWord(elseWord);
        elseExpr.setResultExpr(resultExpr);
        return elseExpr;
    }

    private CastExpression parseCastExpr() {
        String castWord = stream.parseSpecialWordValueAndCheck(SW_CAST);
        int from = stream.getCursor();
        stream.checkIsOpenParent();
        stream.moveCursor();
        SelectableExpression operandExpr = parseSingleSelectExpr();
        String asWord = stream.parseSpecialWordValueAndCheck(SW_AS);
        from = stream.getCursor();
        FuncExpression targetExpr = null;
        if (checkIsFuncExpression()) {
            targetExpr = parseFuncExpr();
        } else {
            String typeName = stream.parseSpecialWordValue(false);
            if (typeName == null) {
                throw stream.createException("Ожидается имя типа в CAST функции", from);
            }
            targetExpr = new FuncExpression();
            targetExpr.setName(typeName);
        }
        from = stream.getCursor();
        stream.checkIsCloseParen();
        stream.moveCursor();
        CastExpression castExpr = new CastExpression();
        castExpr.setCastWord(castWord);
        castExpr.setOperandExpr(operandExpr);
        castExpr.setAsWord(asWord);
        castExpr.setTargetExpr(targetExpr);
        return castExpr;
    }

    private SelectQuery parseSubSelect() {
        stream.skipSpaces();
        if ('(' != stream.getSymbol()) {
            throw stream.createException("Ожидается начало подзапроса символом '(', а получен '" + stream.getSymbol() + "'", stream
                    .getCursor());
        }
        stream.moveCursor(); // skip '('
        SelectQuery subSelectQuery = parseSingleSelectQuery();
        stream.skipSpaces();
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается заврешение подзапроса символом ')', а получен '" + stream.getSymbol() + "'", stream
                    .getCursor());
        }
        stream.moveCursor(); // skip ')'
        subSelectQuery.setWrapped(true);
        return subSelectQuery;
    }

    private ColumnExpression parseColumnExpr() {
        String prefix = null;
        String columnName = null;
        int from = stream.getCursor();
        String value = stream.parseIdentifierValue();
        if (value == null) {
            throw stream.createException("Не является именем колонки", from);
        }
        char symbol = stream.getSymbol();
        if ('.' == symbol) {
            prefix = value;
            stream.moveCursor();
            if (stream.checkIsAsteriskValue()) {
                stream.moveCursor();
                columnName = stream.getValueFrom(stream.getCursor() - 1);
            } else {
                from = stream.getCursor();
                columnName = stream.parseIdentifierValue();
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

    private TableReferenceExpression parseTableReference(boolean useJoin) {
        if (useJoin && stream.checkIsSpecialWordValue()) {
            return parseTableJoinExpression();
        }
        if (checkIsSubSelectQuery()) {
            return parseTableSubQuery();
        }
        if (checkIsTableExpression()) {
            return parseTableExpression();
        }
        if (checkIsTableValuesExpression()) {
            return parseTableValuesExpression();
        }
        throw stream.createException("Неизвестный тип данных о таблицах", stream.getCursor());
    }

    private TableReferenceExpression parseTableSubQuery() {
        SelectQuery selectQuery = parseSubSelect();
        TableSubQueryExpression tableSubQuery = new TableSubQueryExpression();
        tableSubQuery.setSubQueryExpr(selectQuery);
        tableSubQuery.setAsClauseExpr(parseAsClauseExpression(false));
        return tableSubQuery;
    }

    private TableValuesExpression parseTableValuesExpression() {
        stream.skipSpaces();
        int from = stream.getCursor();
        if ('(' != stream.getSymbol()) {
            throw stream.createException("Ожидается начало секции '(VALUES', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        String valuesWord = stream.parseSpecialWordValueAndCheck(SW_VALUES);
        TableValuesExpression tableValuesExpr = new TableValuesExpression();
        tableValuesExpr.setValuesWord(valuesWord);
        from = stream.getCursor();
        ValueListExpression valueExpr = parseValueListExpression();
        tableValuesExpr.addValuesExpr(valueExpr);
        stream.skipSpaces();
        while (',' == stream.getSymbol()) {
            stream.moveCursor();
            valueExpr = parseValueListExpression();
            tableValuesExpr.addValuesExpr(valueExpr);
            stream.skipSpaces();
        }
        from = stream.getCursor();
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается конец секции VALUES ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        String asWord = stream.parseSpecialWordValueVariants(SW_AS);
        tableValuesExpr.setAsWord(asWord);
        FuncExpression templateExpr = null;
        if (checkIsFuncExpression()) {
            templateExpr = parseFuncExpr();
        } else {
            from = stream.getCursor();
            String templateName = stream.parseSpecialWordValue(false);
            if (templateName == null) {
                throw stream.createException("Ожидается имя шаблона для VALUES выражения", from);
            }
            templateExpr = new FuncExpression();
            templateExpr.setName(templateName);
        }
        tableValuesExpr.setTemplateExpr(templateExpr);
        return tableValuesExpr;
    }

    private TableExpression parseTableExpression() {
        ColumnExpression columnExpr = parseColumnExpr();
        TableExpression tableExpr = new TableExpression();
        tableExpr.setSchemeName(columnExpr.getPrefix());
        tableExpr.setTableName(columnExpr.getColumnName());
        tableExpr.setAsClauseExpr(parseAsClauseExpression(false));
        return tableExpr;
    }

    private TableJoinExpression parseTableJoinExpression() {
        TableJoinExpression tableJoinExpr = new TableJoinExpression();
        String joinWords = parseJoinWords();
        tableJoinExpr.setJoinWords(joinWords);
        TableJoinTypes joinType = resolveJoinType(joinWords);
        tableJoinExpr.setJoinType(joinType);
        tableJoinExpr.setTableRefExpr(parseTableReference(false));
        if (TJT_CROSS_JOIN != joinType) {
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
            if (isCloseBrace()) {
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
        char symbol = stream.getSymbol();
        return '(' == symbol && !checkIsSubSelectQuery();
    }

    private boolean isCloseBrace() {
        char symbol = stream.getSymbol();
        return ')' == symbol;
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
            if (isCloseBrace()) {
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
                parentLevel.add(currentLevel.combine(true));
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
            return parseValueExpr();
        }
        if (checkIsFuncExpression()) {
            return parseFuncExpr();
        }
        if (checkIsCaseExpression()) {
            return parseCaseExpr();
        }
        if (checkIsSubSelectQuery()) {
            return parseSubSelect();
        }
        if (checkIsColumnExpression()) {
            return parseColumnExpr();
        }
        if (checkIsCaseExpression()) {
            return parseCaseExpr();
        }
        throw stream.createException("Неизвестное выражение для условия поиска <row value constructor>", stream.getCursor());
    }

    /*** TODO переписать */
    private ExistsPredicateExpression parseExistsPredicateExpr() {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String existsWord = stream.parseSpecialWordValueAndCheck(SW_EXISTS);
        if (!checkIsSubSelectQuery()) {
            throw stream.createException("Ожидается подзапрос в 'EXISTS' выражении", stream.getCursor());
        }
        SelectQuery subSelectExpr = parseSubSelect();
        ExistsPredicateExpression existsExpr = new ExistsPredicateExpression();
        existsExpr.setNotWord(notWord);
        existsExpr.setExistsWord(existsWord);
        existsExpr.setSelectQuery(subSelectExpr);
        return existsExpr;
    }

    private UniquePredicateExpression parseUniquePredicateExpression() {
        String uniqueWord = stream.parseSpecialWordValueAndCheck(SW_UNIQUE);
        SelectQuery subSelectExpr = parseSubSelect();
        UniquePredicateExpression uniquePredicateExpr = new UniquePredicateExpression();
        uniquePredicateExpr.setUniqueWord(uniqueWord);
        uniquePredicateExpr.setSubQuery(subSelectExpr);
        return uniquePredicateExpr;
    }

    private QuantifiedComparisonPredicateExpression completeQuantifiedComparisonPredicateExpression(Expression expression) {
        ComparisonOperator comparisonOperationType = parseComparisonOperatorType();
        String quantifierWord = stream.parseSpecialWordValueAndCheckOneOf(SW_ANY, SW_SOME, SW_ALL);
        SelectQuery subSelectQuery = parseSubSelect();
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
            return parseSubSelect();
        }
        return parseValueListExpression();
    }

    /*** TODO переписать */
    private ValueListExpression parseValueListExpression() {
        stream.skipSpaces();
        int from = stream.getCursor();
        char symol = stream.getSymbol();
        if ('(' != symol) {
            throw stream.createException("Ожидается начало блока значений '(', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        from = stream.getCursor();
        ValueListExpression inValueExpr = new ValueListExpression();
        SelectableExpression valueExpr = parseSingleSelectExpr();
        while (true) {
            inValueExpr.addValueExpr(valueExpr);
            char symbol = stream.parseFuncArgsDelim();
            if (')' == symbol) {
                break;
            }
            if (',' == symbol) {
                stream.moveCursor();
                from = stream.getCursor();
                valueExpr = parseSingleSelectExpr();
            } else {
                throw stream.createException("Ожидается разделитель между значениями в блоке ',', а получен '" + symbol + "'", from);
            }
        }
        stream.skipSpaces();
        from = stream.getCursor();
        if (stream.getSymbol() != ')') {
            throw stream.createException("Ожидается заврешение блока значений ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        return inValueExpr;
    }

    private LikePredicateExpression completeLikePredicateExpression(Expression baseExpression) {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String likeWord = stream.parseSpecialWordValueAndCheck(SW_LIKE);
        SelectableExpression patternExpr = parseSingleSelectExpr();
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
        Expression escapeValueExpr = parseSingleSelectExpr();
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

    /*** TODO переписать */
    private ComparisonPredicateExpression completeValuesComparisonPredicateExpression(Expression firstExpr, ParenLevels levels) {
        int from = stream.getCursor();
        if (levels.isEmpty()) {
            throw stream.createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        ValueListExpression leftValuesExpr = new ValueListExpression();
        leftValuesExpr.addValueExpr(firstExpr);
        stream.skipSpaces();
        while (',' == stream.getSymbol()) {
            stream.moveCursor();
            from = stream.getCursor();
            Expression valueExpr = parseSelectableExpression();
            stream.skipSpaces();
            leftValuesExpr.addValueExpr(valueExpr);
        }
        from = stream.getCursor();
        if (')' != stream.getSymbol()) {
            throw stream.createException(
                    "Ожидается заврешение <row_value_constructor> символом ')', а получен '" + stream.getSymbol() + "'", from);
        }
        ParenLevel level = levels.pop();
        if (!level.isEmpty()) {
            throw stream.createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        levels.decDepth();
        stream.moveCursor();
        ComparisonOperator operType = parseComparisonOperatorType();
        ValueListExpression rightExpr = parseValueListExpression();
        ComparisonPredicateExpression compPredicateExpr = new ComparisonPredicateExpression();
        compPredicateExpr.setLeftExpr(leftValuesExpr);
        compPredicateExpr.setCompOperatorType(operType);
        compPredicateExpr.setRightExpr(rightExpr);
        return compPredicateExpr;
    }

    private GroupingColumnReferenceExpression parseGroupingColumnReferenceExpression() {
        ColumnExpression columnExpr = parseColumnExpr();
        GroupingColumnReferenceExpression refColumnExpr = new GroupingColumnReferenceExpression();
        refColumnExpr.setColumnRefExpr(columnExpr);
        return refColumnExpr;
    }

    private SortKeyExpression parseSortKeyExpression() {
        int from = stream.getCursor();
        Expression sortKeyValueExpr = null;
        if (checkIsColumnExpression()) {
            sortKeyValueExpr = parseColumnExpr();
            // TODO проверить, что это простая колонка
        } else if (checkIsNumericExpression()) {
            sortKeyValueExpr = parseNumericExpr();
            // TODO проверить, что это число без мантис точек и прочего лишнего
        }
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
        Expression collationNameExpr = parseColumnExpr();
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
            ValueExpression valueExpr = parseValueExpr();
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
            ValueExpression valueExpr = parseValueExpr();
            offsetExpr.setOffsetExpr(valueExpr);
        } else {
            throw stream.createException("Ожидается выражение значения в секции 'OFFSET'", from);
        }
        return offsetExpr;
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

    private boolean checkIsCaseExpression() {
        return stream.checkIsSpecialWordValueSame(SW_CASE);
    }

    private boolean checkIsWhenThenExpression() {
        return stream.checkIsSpecialWordValueSame(SW_WHEN);
    }

    private boolean checkIsElseExpression() {
        return stream.checkIsSpecialWordValueSame(SW_ELSE);
    }

    private boolean checkIsCastExpression() {
        return stream.checkIsSpecialWordValueSame(SW_CAST);
    }

    private boolean checkIsUniquePredicateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_UNIQUE);
    }

    private boolean checkIsConstExpression() {
        return stream.checkIsSpecialWordValueOneOf(SW_NULL, SW_TRUE, SW_FALSE);
    }

    private boolean checkIsDateExpression() {
        return stream.checkIsSpecialWordValueOneOf(SW_TIME, SW_DATE, SW_TIMESTAMP, SW_INTERVAL);
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

    // -------------------------------------------------------------- SIMPLE ----------------------------------------------------------------------------- \\
    private boolean checkIsAsteriskExpression() {
        return stream.checkIsAsteriskValue();
    }

    private boolean checkIsNumericExpression() {
        return stream.checkIsNumericValue();
    }

    private boolean checkIsStringExpression() {
        return stream.checkIsStringValue();
    }

    private boolean checkIsQuestionExpression() {
        return stream.checkIsQuestionValue();
    }

    private boolean checkIsFilterExpression() {
        return stream.checkIsFilterValue();
    }

    private boolean checkIsColumnExpression() {
        return stream.checkIsIdentifierValue(true);
    }

    private boolean checkIsValuesComparisonPredicateExpression() {
        return stream.checkIsDelim(',', false);
    }

    private boolean checkIsTableExpression() {
        return stream.checkIsIdentifierValue(true);
    }

    private boolean checkIsTableJoinExpression(boolean useJoin) {
        return useJoin && stream.checkIsSpecialWordValueOneOf(SW_INNER, SW_LEFT, SW_RIGHT, SW_FULL, SW_CROSS);
    }

    // ----------------------------------------------------------- OPERATOR ----------------------------------------------------------------------------- \\
    private boolean checkIsMathOperator() {
        return stream.checkIsMathOperatorValue();
    }

    private boolean checkIsComparisonOperator() {
        return stream.checkIsComparisonOperatorValue();
    }

    // ----------------------------------------------------------- COMPOSITE ----------------------------------------------------------------------------- \\
    private boolean checkIsValueExpression() {
        if (checkIsNumericExpression()) {
            return true;
        }
        if (checkIsStringExpression()) {
            return true;
        }
        if (checkIsDateExpression()) {
            return true;
        }
        if (checkIsConstExpression()) {
            return true;
        }
        if (checkIsQuestionExpression()) {
            return true;
        }
        if (checkIsFilterExpression()) {
            return true;
        }
        return false;
    }

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

    private boolean checkIsTableReferenceExpression(boolean useJoin) {
        if (checkIsTableJoinExpression(useJoin)) {
            return true;
        }
        if (checkIsSubSelectQuery()) {
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

    // ----------------------------------------------------------- LOGIC ----------------------------------------------------------------------------- \\
    private boolean checkIsFuncExpression() {
        stream.keepParserState();
        String id = stream.parseSpecialWordValue();
        boolean funcFlag = id != null && !isSqlWord(id) && stream.checkIsOpenParent(false);
        stream.rollbackParserState();
        return funcFlag;
    }

    private boolean checkIsSubSelectQuery() {
        boolean subSelectFlag = stream.checkIsOpenParent(false);
        if (subSelectFlag) {
            stream.moveCursor();
            subSelectFlag = stream.checkIsSpecialWordValueSame(SW_SELECT);
            stream.moveCursor(-1);
        }
        return subSelectFlag;
    }

    private boolean checkIsTableValuesExpression() {
        stream.skipSpaces();
        stream.keepParserState();
        char symbol = stream.getSymbol();
        if ('(' != symbol) {
            stream.rollbackParserState();
            return false;
        }
        stream.moveCursor();
        String word = stream.parseSpecialWordValue();
        stream.rollbackParserState();
        return SW_VALUES.equalsIgnoreCase(word);
    }

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
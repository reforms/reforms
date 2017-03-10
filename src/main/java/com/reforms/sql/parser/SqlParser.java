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

import java.util.List;

import static com.reforms.sql.expr.term.ConditionFlowType.resolveConditionFlowType;
import static com.reforms.sql.expr.term.MathOperator.resolveMathOperator;
import static com.reforms.sql.expr.term.SqlWords.*;
import static com.reforms.sql.expr.term.from.TableJoinTypes.TJT_CROSS_JOIN;
import static com.reforms.sql.expr.term.predicate.ComparisonOperatorType.resolveComparisonOperatorType;
import static com.reforms.sql.parser.OptWords.*;

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
 *      - value         (String, Number, NULL, ... etc)
 *
 */
public class SqlParser {

    private AbstractSqlStream stream;

    public SqlParser(String query) {
        stream = new SqlStream(query);
    }

    public SelectQuery parseSelectQuery() {
        SelectQuery directSelectQuery = parseSingleSelectQuery();
        if (!stream.finished()) {
            throw stream.createException("Не удалось до конца разобрать запрос", stream.getCursor());
        }
        return directSelectQuery;
    }

    private SelectQuery parseSingleSelectQuery() {
        SelectQuery selectQuery = new SelectQuery();
        fillFullSelectStatement(selectQuery);
        FromStatement fromStatement = parseFromStatement();
        if (fromStatement != null) {
            selectQuery.setFromStatement(fromStatement);
            selectQuery.setWhereStatement(parseWhereStatement());
            selectQuery.setGroupByStatement(parseGroupByStatement());
            selectQuery.setHavingStatement(parseHavingStatement());
        }
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
            selectQuery.addLinkingQuery(linkedSelectExpr);
        }
        OrderByStatement orderByStatement = parseOrderByStatement();
        selectQuery.setOrderByStatement(orderByStatement);
        PageStatement pageStatement = parsePageStatement();
        selectQuery.setPageStatement(pageStatement);
        return selectQuery;
    }

    private boolean checkIsLinkedWord() {
        return stream.checkIsSpecialWordValueOneOf(SW_UNION, SW_EXCEPT, SW_INTERSECT, SW_MINUS);
    }

    private String parseLinkedWord() {
        return stream.parseSpecialWordValueAndCheckOneOf(SW_UNION, SW_EXCEPT, SW_INTERSECT, SW_MINUS);
    }

    private boolean checkIsAllWord() {
        return stream.checkIsSpecialWordValueSame(SW_ALL);
    }

    private String parseAllWord() {
        return stream.parseSpecialWordValueAndCheck(SW_ALL);
    }

    private void fillFullSelectStatement(SelectQuery selectQuery) {
        // 1. SELECT word
        String selectWord = stream.parseSpecialWordValueAndCheck(SW_SELECT);
        // 2. [ALL | DISTINCT] mode word
        String selectModeWord = parseSelectModeWord();
        // 3. SELECT STATEMENT
        SelectStatement selectStatement = parseSelectStatement();
        selectStatement.setSelectWord(selectWord);
        selectStatement.setModeWord(selectModeWord);
        selectQuery.setSelectStatement(selectStatement);
    }

    /** @return ALL или DISTINCT или null */
    private String parseSelectModeWord() {
        return stream.parseSpecialWordValueVariants(SW_ALL, SW_DISTINCT);
    }

    private SelectStatement parseSelectStatement() {
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
        return selectStatement;
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

    private boolean checkIsMathOperator() {
        return stream.checkIsMathOperatorValue();
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
        if (checkIsAsteriskExpr()) {
            return parseAsteriskExpr();
        }
        if (checkIsFuncExpr()) {
            return parseFuncExpr();
        }
        if (checkIsValueExpr()) {
            return parseValueExpr();
        }
        if (checkIsCaseExpr()) {
            return parseCaseExpr();
        }
        if (checkIsCastExpr()) {
            return parseCastExpr();
        }
        if (checkIsSubSelect()) {
            return parseSubSelect();
        }
        if (checkIsColumnExpr()) {
            return parseColumnExpr();
        }
        throw stream.createException("Неизвестное выражение для выборки", stream.getCursor());
    }

    private boolean checkIsAsteriskExpr() {
        return stream.checkIsAsteriskValue();
    }

    private AsteriskExpression parseAsteriskExpr() {
        if (!checkIsAsteriskExpr()) {
            throw stream.createException("Ожидается символ '*'");
        }
        stream.moveCursor();
        return new AsteriskExpression();
    }

    private boolean checkIsFuncExpr() {
        stream.keepParserState();
        String id = stream.parseSpecialWordValue();
        boolean funcFlag = id != null && !isSqlWord(id) && stream.checkIsOpenParent(false);
        stream.rollbackParserState();
        return funcFlag;
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

    private boolean checkIsValueExpr() {
        if (checkIsNumericExpr()) {
            return true;
        }
        if (checkIsStringExpr()) {
            return true;
        }
        if (checkIsDateExpr()) {
            return true;
        }
        if (checkIsIntervalExpr()) {
            return true;
        }
        if (checkIsConstExpr()) {
            return true;
        }
        if (checkIsQuestionExpr()) {
            return true;
        }
        if (checkIsFilterExpr()) {
            return true;
        }
        return false;
    }

    private boolean checkIsNumericExpr() {
        return stream.checkIsNumericValue();
    }

    private NumericExpression parseNumericExpr() {
        int from = stream.getCursor();
        String numericValue = stream.parseNumericValue();
        if (numericValue == null) {
            throw stream.createException("Ожидается числовое значение", from);
        }
        return new NumericExpression(numericValue);
    }

    private boolean checkIsStringExpr() {
        return stream.checkIsStringValue();
    }

    private StringExpression parseStringExpr() {
        int from = stream.getCursor();
        String stringValue = stream.parseStringValue();
        if (stringValue == null) {
            throw stream.createException("Ожидается строковое значение", from);
        }
        return new StringExpression(stringValue);
    }

    private boolean checkIsDateExpr() {
        return false;
    }

    private ValueExpression parseDateExpr() {
        return null;
    }

    private boolean checkIsIntervalExpr() {
        return false;
    }

    private ValueExpression parseIntervalExpr() {
        return null;
    }

    private boolean checkIsConstExpr() {
        return stream.checkIsSpecialWordValueOneOf(SW_NULL, SW_TRUE, SW_FALSE);
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

    private boolean checkIsQuestionExpr() {
        return stream.checkIsQuestionValue();
    }

    private QuestionExpression parseQuestionExpr() {
        if (!checkIsQuestionExpr()) {
            throw stream.createException("Ожидается '?', а получен символ '" + stream.getSymbol() + "'");
        }
        stream.moveCursor();
        return new QuestionExpression();
    }

    private ValueExpression parseValueExpr() {
        if (checkIsNumericExpr()) {
            return parseNumericExpr();
        }
        if (checkIsStringExpr()) {
            return parseStringExpr();
        }
        if (checkIsDateExpr()) {
            return parseDateExpr();
        }
        if (checkIsIntervalExpr()) {
            return parseIntervalExpr();
        }
        if (checkIsConstExpr()) {
            return parseConstExpr();
        }
        if (checkIsQuestionExpr()) {
            return parseQuestionExpr();
        }
        if (checkIsFilterExpr()) {
            return parseFilterExpr();
        }
        return null;
    }

    private boolean checkIsCaseExpr() {
        return stream.checkIsSpecialWordValueSame(SW_CASE);
    }

    private CaseExpression parseCaseExpr() {
        CaseExpression caseExpr = new CaseExpression();
        String caseWord = stream.parseSpecialWordValueAndCheck(SW_CASE);
        caseExpr.setCaseWord(caseWord);
        boolean searchFlag = checkIsSearchCaseExpr();
        if (!searchFlag) {
            Expression operandExpr = parseCaseOperandExpr();
            caseExpr.setOperandExpr(operandExpr);
        }
        while (checkIsWhenThenExpr()) {
            WhenThenExpression whenThenExpr = parseWhenThenExpr(searchFlag);
            caseExpr.addWhenThenExprs(whenThenExpr);
        }
        // TODO: добавить ElseExpression
        if (checkIsElseExpr()) {
            ElseExpression elseExpr = parseElseExpr();
            caseExpr.setElseExpr(elseExpr);
        }
        String endWord = stream.parseSpecialWordValueAndCheck(SW_END);
        caseExpr.setEndWord(endWord);
        return caseExpr;
    }

    private boolean checkIsFilterExpr() {
        return stream.checkIsFilterValue();
    }

    /**
     * TODO: переделать через parseStatementWord(false, FILTER_CHARS);
     * @return
     */
    private FilterExpression parseFilterExpr() {
        int from = stream.getCursor();
        if (!checkIsFilterExpr()) {
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

    private boolean checkIsSearchCaseExpr() {
        return stream.checkIsSpecialWordValueSame(SW_WHEN);
    }

    private boolean checkIsWhenThenExpr() {
        return checkIsSearchCaseExpr();
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

    private boolean checkIsElseExpr() {
        return stream.checkIsSpecialWordValueSame(SW_ELSE);
    }

    private ElseExpression parseElseExpr() {
        String elseWord = stream.parseSpecialWordValueAndCheck(SW_ELSE);
        Expression resultExpr = parseSingleSelectExpr();
        ElseExpression elseExpr = new ElseExpression();
        elseExpr.setElseWord(elseWord);
        elseExpr.setResultExpr(resultExpr);
        return elseExpr;
    }

    private boolean checkIsCastExpr() {
        return stream.checkIsSpecialWordValueSame(SW_CAST);
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
        if (checkIsFuncExpr()) {
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

    private boolean checkIsSubSelect() {
        boolean subSelectFlag = stream.checkIsOpenParent(false);
        if (subSelectFlag) {
            stream.moveCursor();
            subSelectFlag = stream.checkIsSpecialWordValueSame(SW_SELECT);
            stream.moveCursor(-1);
        }
        return subSelectFlag;
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

    private boolean checkIsColumnExpr() {
        return stream.checkIsIdentifierValue(true);
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

    private FromStatement parseFromStatement() {
        if (stream.checkIsSpecialWordValue()) {
            String fromWord = stream.parseSpecialWordValueAndCheck(SW_FROM);
            FromStatement fromStatement = new FromStatement();
            fromStatement.setFromWord(fromWord);
            List<TableReferenceExpression> tableRefExprs = fromStatement.getTableRefExprs();
            while (checkIsTableReference(!tableRefExprs.isEmpty())) {
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

    private boolean checkIsTableReference(boolean useJoin) {
        if (stream.checkIsSpecialWordValue()) {
            stream.keepParserState();
            String word = stream.parseSpecialWordValue();
            stream.rollbackParserState();
            return useJoin && startFrom(word, SW_INNER_JOIN, SW_LEFT_OUTER_JOIN, SW_RIGHT_OUTER_JOIN, SW_FULL_OUTER_JOIN, SW_CROSS_JOIN);
        }
        if (checkIsSubSelect()) {
            return true;
        }
        if (checkIsTableValuesExpression()) {
            return true;
        }
        return checkIsTable();
    }

    private TableReferenceExpression parseTableReference(boolean useJoin) {
        if (useJoin && stream.checkIsSpecialWordValue()) {
            return parseTableJoinExpression();
        }
        if (checkIsSubSelect()) {
            return parseTableSubQuery();
        }
        if (checkIsTable()) {
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
        if (checkIsFuncExpr()) {
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

    private boolean checkIsTable() {
        return stream.checkIsIdentifierValue(true);
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

    private boolean checkIsWhereStatement() {
        return stream.checkIsSpecialWordValueSame(SW_WHERE);
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
        return '(' == symbol && !checkIsSubSelect();
    }

    private boolean isCloseBrace() {
        char symbol = stream.getSymbol();
        return ')' == symbol;
    }

    private Expression parseSearchConditionExpr(ParenLevels levels) {
        if (checkIsRowValueConstructorExpr()) {
            Expression searchExpr = parseSingleRowValueConstructorExpr(levels);
            if (checkIsQuantifiedComparisonPredicateExpression()) {
                QuantifiedComparisonPredicateExpression quanifiedComparisonPredicateExpr = completeQuantifiedComparisonPredicateExpression(searchExpr);
                return quanifiedComparisonPredicateExpr;
            }
            if (checkIsComparisonOperatorType()) {
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
        if (checkIsExistsPredicateExpr()) {
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

    private boolean checkIsRowValueConstructorExpr() {
        if (checkIsValueExpr()) {
            return true;
        }
        if (checkIsFuncExpr()) {
            return true;
        }
        if (checkIsCaseExpr()) {
            return true;
        }
        if (checkIsSubSelect()) {
            return true;
        }
        if (checkIsColumnExpr()) {
            return true;
        }
        return false;
    }

    private boolean checkIsNotWord() {
        return stream.checkIsSpecialWordValueSame(SW_NOT);
    }

    private NotExpression parseNotExpr() {
        String notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        NotExpression notExpr = new NotExpression();
        notExpr.setNotWord(notWord);
        return notExpr;
    }

    private Expression parseRowValueConstructorExpr() {
        if (checkIsValueExpr()) {
            return parseValueExpr();
        }
        if (checkIsFuncExpr()) {
            return parseFuncExpr();
        }
        if (checkIsCaseExpr()) {
            return parseCaseExpr();
        }
        if (checkIsSubSelect()) {
            return parseSubSelect();
        }
        if (checkIsColumnExpr()) {
            return parseColumnExpr();
        }
        if (checkIsCaseExpr()) {
            return parseCaseExpr();
        }
        throw stream.createException("Неизвестное выражение для условия поиска <row value constructor>", stream.getCursor());
    }

    /*** TODO переписать */
    private boolean checkIsExistsPredicateExpr() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_EXISTS);
    }

    /*** TODO переписать */
    private ExistsPredicateExpression parseExistsPredicateExpr() {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String existsWord = stream.parseSpecialWordValueAndCheck(SW_EXISTS);
        if (!checkIsSubSelect()) {
            throw stream.createException("Ожидается подзапрос в 'EXISTS' выражении", stream.getCursor());
        }
        SelectQuery subSelectExpr = parseSubSelect();
        ExistsPredicateExpression existsExpr = new ExistsPredicateExpression();
        existsExpr.setNotWord(notWord);
        existsExpr.setExistsWord(existsWord);
        existsExpr.setSelectQuery(subSelectExpr);
        return existsExpr;
    }

    private boolean checkIsUniquePredicateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_UNIQUE);
    }

    private UniquePredicateExpression parseUniquePredicateExpression() {
        String uniqueWord = stream.parseSpecialWordValueAndCheck(SW_UNIQUE);
        SelectQuery subSelectExpr = parseSubSelect();
        UniquePredicateExpression uniquePredicateExpr = new UniquePredicateExpression();
        uniquePredicateExpr.setUniqueWord(uniqueWord);
        uniquePredicateExpr.setSubQuery(subSelectExpr);
        return uniquePredicateExpr;
    }

    private boolean checkIsQuantifiedComparisonPredicateExpression() {
        if (!checkIsComparisonOperatorType()) {
            return false;
        }
        stream.keepParserState();
        parseComparisonOperatorType();
        String word = stream.parseSpecialWordValue();
        stream.rollbackParserState();
        return SW_ANY.equalsIgnoreCase(word) || SW_SOME.equalsIgnoreCase(word) || SW_ALL.equalsIgnoreCase(word);
    }

    private QuantifiedComparisonPredicateExpression completeQuantifiedComparisonPredicateExpression(Expression expression) {
        ComparisonOperatorType comparisonOperationType = parseComparisonOperatorType();
        String quantifierWord = stream.parseSpecialWordValueAndCheckOneOf(SW_ANY, SW_SOME, SW_ALL);
        SelectQuery subSelectQuery = parseSubSelect();
        QuantifiedComparisonPredicateExpression quanCompPredicateExpr = new QuantifiedComparisonPredicateExpression();
        quanCompPredicateExpr.setBaseExpr(expression);
        quanCompPredicateExpr.setCompOperatorType(comparisonOperationType);
        quanCompPredicateExpr.setQuantifierWord(quantifierWord);
        quanCompPredicateExpr.setSubSelectQuery(subSelectQuery);
        return quanCompPredicateExpr;
    }

    private boolean checkIsComparisonOperatorType() {
        return stream.checkIsComparisonOperatorValue();
    }

    private ComparisonOperatorType parseComparisonOperatorType() {
        int from = stream.getCursor();
        String operatorValue = stream.parseComparisonOperatorValue();
        ComparisonOperatorType operator = resolveComparisonOperatorType(operatorValue);
        if (operator == null) {
            throw stream.createException("Ожидается операция сравнения '=' или '<' или '<=' или '>' или '>=' или '<>' или '!='", from);
        }
        return operator;
    }

    private boolean checkIsNullablePredicateExpression() {
        return stream.checkIsSpecialWordValueSame(SW_IS);
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

    private boolean checkIsInPredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_IN);
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
        if (checkIsSubSelect()) {
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

    private boolean checkIsLikePredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_LIKE);
    }

    private LikePredicateExpression completeLikePredicateExpression(Expression baseExpression) {
        String notWord = null;
        if (checkIsNotWord()) {
            notWord = stream.parseSpecialWordValueAndCheck(SW_NOT);
        }
        String likeWord = stream.parseSpecialWordValueAndCheck(SW_LIKE);
        SelectableExpression patternExpr = parseSingleSelectExpr();
        EscapeExpression escapeExpr = null;
        if (checkIsEscapeWord()) {
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

    private boolean checkIsEscapeWord() {
        return stream.checkIsSpecialWordValueSame(SW_ESCAPE);
    }

    private EscapeExpression parseEscapeExpression() {
        String escapeWord = stream.parseSpecialWordValueAndCheck(SW_ESCAPE);
        Expression escapeValueExpr = parseSingleSelectExpr();
        EscapeExpression escapeExpr = new EscapeExpression();
        escapeExpr.setEscapeWord(escapeWord);
        escapeExpr.setEscapeValueExpr(escapeValueExpr);
        return escapeExpr;
    }

    private boolean checkIsBetweenPredicateExpression() {
        return stream.checkIsSpecialWordSequents(OW_O_NOT, OW_R_BETWEEN);
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
        ComparisonOperatorType operType = parseComparisonOperatorType();
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

    private boolean checkIsValuesComparisonPredicateExpression() {
        stream.skipSpaces();
        return ',' == stream.getSymbol();
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
        ComparisonOperatorType operType = parseComparisonOperatorType();
        ValueListExpression rightExpr = parseValueListExpression();
        ComparisonPredicateExpression compPredicateExpr = new ComparisonPredicateExpression();
        compPredicateExpr.setLeftExpr(leftValuesExpr);
        compPredicateExpr.setCompOperatorType(operType);
        compPredicateExpr.setRightExpr(rightExpr);
        return compPredicateExpr;
    }

    private boolean checkIsGroupByStatement() {
        return stream.checkIsSpecialWordSequents(OW_R_GROUP, OW_R_BY);
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

    private GroupingColumnReferenceExpression parseGroupingColumnReferenceExpression() {
        ColumnExpression columnExpr = parseColumnExpr();
        GroupingColumnReferenceExpression refColumnExpr = new GroupingColumnReferenceExpression();
        refColumnExpr.setColumnRefExpr(columnExpr);
        return refColumnExpr;
    }

    private boolean checkIsHavingStatement() {
        return stream.checkIsSpecialWordValueSame(SW_HAVING);
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

    private boolean checkIsOrderByStatement() {
        return stream.checkIsSpecialWordSequents(OW_R_ORDER, OW_R_BY);
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

    private SortKeyExpression parseSortKeyExpression() {
        int from = stream.getCursor();
        Expression sortKeyValueExpr = null;
        if (checkIsColumnExpr()) {
            sortKeyValueExpr = parseColumnExpr();
            // TODO проверить, что это простая колонка
        } else if (checkIsNumericExpr()) {
            sortKeyValueExpr = parseNumericExpr();
            // TODO проверить, что это число без мантис точек и прочего лишнего
        }
        if (sortKeyValueExpr == null) {
            throw stream.createException("Ожидается значение сортировки в секции 'ORDER BY'", from);
        }
        SortKeyExpression sortKeyExpr = new SortKeyExpression();
        sortKeyExpr.setSortKeyValueExpr(sortKeyValueExpr);
        if (checkIsCollateWord()) {
            CollateExpression collateExpr = parseCollateExpression();
            sortKeyExpr.setCollateExpr(collateExpr);
        }
        if (checkIsOrderingSpecification()) {
            String orderingSpec = parseOrderingSpecification();
            sortKeyExpr.setOrderingSpec(orderingSpec);
        }
        return sortKeyExpr;
    }

    private boolean checkIsCollateWord() {
        return stream.checkIsSpecialWordValueSame(SW_COLLATE);
    }

    private CollateExpression parseCollateExpression() {
        String collateWord = stream.parseSpecialWordValueAndCheck(SW_COLLATE);
        Expression collationNameExpr = parseColumnExpr();
        CollateExpression collateExpr = new CollateExpression();
        collateExpr.setCollateWord(collateWord);
        collateExpr.setCollationNameExpr(collationNameExpr);
        return collateExpr;
    }

    private boolean checkIsOrderingSpecification() {
        return stream.checkIsSpecialWordValueOneOf(SW_ASC, SW_DESC);
    }

    private String parseOrderingSpecification() {
        return stream.parseSpecialWordValueAndCheckOneOf(SW_ASC, SW_DESC);
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

    private boolean checkIsLimitExpression() {
        return stream.checkIsSpecialWordValueSame(SW_LIMIT);
    }

    private LimitExpression parseLimitExpression() {
        if (!checkIsLimitExpression()) {
            return null;
        }
        String limitWord = stream.parseSpecialWordValueAndCheck(SW_LIMIT);
        LimitExpression limitExpr = new LimitExpression();
        limitExpr.setLimitWord(limitWord);
        int from = stream.getCursor();
        if (checkIsValueExpr()) {
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

    private boolean checkIsOffsetExpression() {
        return stream.checkIsSpecialWordValueSame(SW_OFFSET);
    }

    private OffsetExpression parseOffsetExpression() {
        if (!checkIsOffsetExpression()) {
            return null;
        }
        String offsetWord = stream.parseSpecialWordValueAndCheck(SW_OFFSET);
        OffsetExpression offsetExpr = new OffsetExpression();
        offsetExpr.setOffsetWord(offsetWord);
        int from = stream.getCursor();
        if (checkIsValueExpr()) {
            ValueExpression valueExpr = parseValueExpr();
            offsetExpr.setOffsetExpr(valueExpr);
        } else {
            throw stream.createException("Ожидается выражение значения в секции 'OFFSET'", from);
        }
        return offsetExpr;
    }
}
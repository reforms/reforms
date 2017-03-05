package com.reforms.sql.parser;

import com.reforms.sql.expr.query.LinkingSelectQuery;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.*;
import com.reforms.sql.expr.term.casee.CaseExpression;
import com.reforms.sql.expr.term.casee.WhenThenExpression;
import com.reforms.sql.expr.term.from.*;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.term.predicate.*;
import com.reforms.sql.expr.term.value.*;

import java.util.*;

import static com.reforms.sql.expr.term.ConditionFlowType.resolveConditionFlowType;
import static com.reforms.sql.expr.term.MathOperator.MO_CONCAT;
import static com.reforms.sql.expr.term.MathOperator.resolveMathOperator;
import static com.reforms.sql.expr.term.SqlWords.*;
import static com.reforms.sql.expr.term.from.TableJoinTypes.TJT_CROSS_JOIN;
import static com.reforms.sql.expr.term.predicate.ComparisonOperatorType.*;

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

    private static final char EOF = '\0';

    private ISqlStream stream;

    public SqlParser(String query) {
        this.stream = new SqlStream(query);
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
            String linkedWord = parseStatementWord();
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
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_UNION.equalsIgnoreCase(word) || SW_EXCEPT.equalsIgnoreCase(word)
                || SW_INTERSECT.equalsIgnoreCase(word) || SW_MINUS.equalsIgnoreCase(word);
    }

    private boolean checkIsAllWord() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_ALL.equalsIgnoreCase(word);
    }

    private String parseAllWord() {
        int from = stream.getCursor();
        String word = parseStatementWord();
        if (!SW_ALL.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается ключевое слово 'ALL', а получено '" + word + "'", from);
        }
        return word;
    }

    private void fillFullSelectStatement(SelectQuery selectQuery) {
        // 1. SELECT word
        int from = stream.getCursor();
        String selectWord = parseStatementWord();
        if (!SW_SELECT.equalsIgnoreCase(selectWord)) {
            throw stream.createException("Ожидается ключевое слово 'SELECT', а получено '" + selectWord + "'", from);
        }
        // 2. [ALL | DISTINCT] mode word
        String selectModeWord = parseSelectModeWord();
        // 3. SELECT STATEMENT
        SelectStatement selectStatement = parseSelectStatement();
        selectStatement.setModeWord(selectModeWord);
        selectQuery.setSelectStatement(selectStatement);
    }

    private String parseSelectModeWord() {
        stream.keepParserState();
        String modeWord = parseStatementWord();
        if (SW_ALL.equalsIgnoreCase(modeWord) || SW_DISTINCT.equalsIgnoreCase(modeWord)) {
            stream.skipParserState();
            return modeWord;
        } else {
            stream.rollbackParserState();
        }
        return null;
    }

    private SelectStatement parseSelectStatement() {
        SelectStatement selectStatement = new SelectStatement();
        SelectableExpression selectExpr = parseFullSelectableExpression();
        if (selectExpr == null) {
            throw stream.createException("В выражении SELECT должен быть хотя бы 1 параметр выборки", stream.getCursor());
        }
        while (selectExpr != null) {
            selectStatement.addSelectExpression(selectExpr);
            char symbol = parseDelim(FUNC_ARGS_DELIMS, false);
            if (EOF == symbol) {
                break;
            }
            if (')' == symbol) {
                break;
            }
            if (',' == symbol) {
                stream.moveCursor();
            } else if (checkIsSqlWord()) {
                break;
            }
            selectExpr = parseFullSelectableExpression();
        }
        return selectStatement;
    }

    private SelectableExpression parseFullSelectableExpression() {
        skipSpaces();
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
        skipSpaces();
        if (!isOpenBrace()) {
            levels.push(new ParenLevel(false));
        }
        while (true) {
            skipSpaces();
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

    private static final List<Character> MATH_OPERAND = Arrays.asList('+', '-', '*', '/', '|');

    private boolean checkIsMathOperator() {
        skipSpaces();
        char symbol = stream.getSymbol();
        return MATH_OPERAND.contains(symbol);
    }

    private MathOperator parseMathOperator() {
        skipSpaces();
        char symbol = stream.getSymbol();
        if (!MATH_OPERAND.contains(symbol)) {
            throw stream.createException("Некорректный математический оператор '" + symbol + "'", stream.getCursor());
        }
        stream.moveCursor();
        if ('|' != symbol) {
            return resolveMathOperator("" + symbol);
        }
        char secondSymbol = stream.getSymbol();
        if ('|' != secondSymbol) {
            throw stream.createException("Некорректный оператор конкатинации '" + secondSymbol + "'", stream.getCursor());
        }
        stream.moveCursor();
        return MO_CONCAT;
    }

    private SelectableExpression parseSelectableExpression() {
        skipSpaces();
        if (checkIsAsterisk()) {
            return parseAsterisk();
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

    private boolean checkIsAsterisk() {
        skipSpaces();
        char symbol = stream.getSymbol();
        return '*' == symbol;
    }

    private AsteriskExpression parseAsterisk() {
        AsteriskExpression asteriskExpr = new AsteriskExpression();
        stream.moveCursor();
        return asteriskExpr;
    }

    private boolean checkIsFuncExpr() {
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        boolean funcFlag = wordInfo != null && !wordInfo.isSqlWord() && '(' == wordInfo.getStopSymbol();
        stream.rollbackParserState();
        return funcFlag;
    }

    private static final List<Character> FUNC_ARGS_DELIMS = Arrays.asList(',', ')');

    private FuncExpression parseFuncExpr() {
        FuncExpression funcExpr = new FuncExpression();
        int from = stream.getCursor();
        String funcName = parseStatementWord(false);
        if (funcName.isEmpty()) {
            throw stream.createException("Ожидается наименование функции", from);
        }
        funcExpr.setName(funcName);
        skipSpaces();
        from = stream.getCursor();
        char symol = stream.getSymbol();
        if ('(' != symol) {
            throw stream.createException("Ожидается начало функции '(', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        String quantifier = parseSelectModeWord();
        funcExpr.setQuantifier(quantifier);
        from = stream.getCursor();
        skipSpaces();
        symol = stream.getSymbol();
        if (')' != symol) {
            from = stream.getCursor();
            SelectableExpression arg = parseSingleSelectExpr();
            while (true) {
                funcExpr.addArg(arg);
                char symbol = parseDelim(FUNC_ARGS_DELIMS, true);
                if (')' == symbol) {
                    break;
                }
                if (',' != symbol) {
                    throw stream.createException("Ожидается разделитель между аргументами функции ',', а получен '" + symbol + "'", from);
                }
                stream.moveCursor();
                from = stream.getCursor();
                arg = parseSingleSelectExpr();
            }
            skipSpaces();
        }
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается заврешение функции символом ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        return funcExpr;
    }

    private AsClauseExpression parseAsClauseExpression(boolean selectExpr) {
        skipSpaces();
        stream.keepParserState();
        List<Character> chars = selectExpr ? DONT_SQL_WORD_LETTERS_EXCLUDE_CLAUSE : DONT_SQL_WORD_LETTERS;
        int from = stream.getCursor();
        AsClauseExpression asClauseExpr = new AsClauseExpression();
        String word = parseStatementWord(false, chars);
        if (word.isEmpty()) {
            word = parseDoubleQuoteValue();
        }
        if (SW_AS.equalsIgnoreCase(word)) {
            asClauseExpr.setAsWord(word);
            from = stream.getCursor();
            word = parseStatementWord(false, chars);
            if (word.isEmpty()) {
                word = parseDoubleQuoteValue();
                if (word.isEmpty()) {
                    throw stream.createException("После ключеого слова 'AS' ожидается алиас", from);
                }
            }
            stream.skipParserState();
            asClauseExpr.setAlias(word);
            return asClauseExpr;
        } else if (!isSqlWord(word) && !word.isEmpty()) {
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
        skipSpaces();
        char symbol = stream.getSymbol();
        return symbol == '-' || symbol == '+' || Character.isDigit(symbol);
    }

    private NumericExpression parseNumericExpr() {
        skipSpaces();
        int from = stream.getCursor();
        boolean wasDot = false;
        boolean wasE = false;
        while (true) {
            char symbol = stream.getSymbol();
            if (('+' == symbol || '-' == symbol) && from == stream.getCursor()) {
                stream.moveCursor();
                continue;
            }
            if (Character.isDigit(symbol)) {
                stream.moveCursor();
                continue;
            }
            if ('.' == symbol && !(wasDot || wasE)) {
                wasDot = true;
                stream.moveCursor();
                continue;
            }
            if ('E' == symbol && !wasE) {
                stream.moveCursor();
                char signSymbol = stream.getSymbol();
                if ('+' == signSymbol || '-' == signSymbol) {
                    stream.moveCursor();
                    char digitSymbol = stream.getSymbol();
                    if (Character.isDigit(digitSymbol)) {
                        wasE = true;
                        continue;
                    }
                    stream.moveCursor(-1);
                }
                stream.moveCursor(-1);
            }
            break;
        }
        if (from == stream.getCursor()) {
            throw stream.createException("Не является числом", stream.getCursor());
        }
        char prevSymbol = stream.getSymvol(-1);
        if (stream.getCursor() - from == 1 && ('+' == prevSymbol || '-' == prevSymbol)) {
            throw stream.createException("Ожидается после знака '+' или '-' хотя бы 1 число!", from);
        }
        String numericValue = stream.getPartFrom(from);
        return new NumericExpression(numericValue);
    }

    private boolean checkIsStringExpr() {
        skipSpaces();
        char symbol = stream.getSymbol();
        return '\'' == symbol;
    }

    private StringExpression parseStringExpr() {
        skipSpaces();
        int from = stream.getCursor();
        char symbol = stream.getSymbol();
        if ('\'' == symbol) {
            stream.moveCursor();
            while ('\'' != (symbol = stream.getSymbol()) && symbol != '\0') {
                stream.moveCursor();
            }
        }
        if (symbol == '\0') {
            throw stream.createException("Не является строкой", from);
        }
        stream.moveCursor();
        String stringValue = stream.getPartFrom(from);
        return new StringExpression(stringValue);
    }

    private String parseDoubleQuoteValue() {
        skipSpaces();
        int from = stream.getCursor();
        char symbol = stream.getSymbol();
        if ('"' != symbol) {
            return "";
        }
        stream.moveCursor();
        while ('"' != (symbol = stream.getSymbol()) && symbol != '\0') {
            stream.moveCursor();
        }
        if (symbol == '\0') {
            throw stream.createException("Не является строкой в двойных кавычках", from);
        }
        stream.moveCursor();
        String doubleQuoteValue = stream.getPartFrom(from);
        return doubleQuoteValue;
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
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        stream.rollbackParserState();
        if (wordInfo == null) {
            return false;
        }
        String constWord = wordInfo.getWord();
        return SW_NULL.equalsIgnoreCase(constWord) || SW_TRUE.equalsIgnoreCase(constWord) || SW_FALSE.equalsIgnoreCase(constWord);
    }

    private ValueExpression parseConstExpr() {
        String word = parseStatementWord();
        if (SW_NULL.equalsIgnoreCase(word)) {
            return new NullExpression();
        }
        if (SW_TRUE.equalsIgnoreCase(word)) {
            return new TrueExpression();
        }
        if (SW_FALSE.equalsIgnoreCase(word)) {
            return new FalseExpression();
        }
        throw stream.createException("Ожидается 'NULL' или 'TRUE' или 'FALSE', а получено '" + word + "'", stream.getCursor());
    }

    private boolean checkIsQuestionExpr() {
        skipSpaces();
        char symbol = stream.getSymbol();
        return symbol == '?';
    }

    private QuestionExpression parseQuestionExpr() {
        skipSpaces();
        char symbol = stream.getSymbol();
        if ('?' != symbol) {
            throw stream.createException("Ожидается '?', а получен символ '" + symbol + "'", stream.getCursor());
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
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_CASE.equalsIgnoreCase(word);
    }

    private CaseExpression parseCaseExpr() {
        String word = parseStatementWord();
        int from = stream.getCursor();
        if (!SW_CASE.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'CASE', а получено '" + word + "'", from);
        }
        boolean searchFlag = checkIsSearchCaseExpr();
        return parseDirectCaseExpression(searchFlag);
    }

    private boolean checkIsFilterExpr() {
        skipSpaces();
        return ':' == stream.getSymbol();
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
        int startExprCursor = stream.getCursor();
        while (':' == stream.getSymbol()) {
            stream.moveCursor();
        }
        from = stream.getCursor();
        int filterNameCursor = stream.getCursor();
        while (EOF != stream.getSymbol() &&
                (Character.isJavaIdentifierPart(stream.getSymbol()) || '.' == stream.getSymbol() || '#' == stream.getSymbol())) {
            stream.moveCursor();
        }
        if (filterNameCursor == stream.getCursor()) {
            throw stream.createException("Не является фильтром", from);
        }
        from = stream.getCursor();
        String filterName = stream.getPartFrom(filterNameCursor);
        boolean questionFlag = false;
        if ('?' == stream.getSymbol()) {
            questionFlag = true;
            stream.moveCursor();
        }
        String filterValue = stream.getPartFrom(startExprCursor);
        FilterExpression filterExpr = new FilterExpression(filterValue);
        filterExpr.setColonCount(filterNameCursor - startExprCursor);
        filterExpr.setFilterName(filterName);
        filterExpr.setQuestionFlag(questionFlag);
        return filterExpr;
    }

    private boolean checkIsSearchCaseExpr() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_WHEN.equalsIgnoreCase(word);
    }

    private CaseExpression parseDirectCaseExpression(boolean serachFlag) {
        CaseExpression caseExpr = new CaseExpression();
        if (!serachFlag) {
            Expression operandExpr = parseCaseOperandExpr();
            caseExpr.setOperandExpr(operandExpr);
        }
        while (checkIsWhenThenExpr()) {
            WhenThenExpression whenThenExpr = parseWhenThenExpr(serachFlag);
            caseExpr.addWhenThenExprs(whenThenExpr);
        }
        if (checkIsElseExpr()) {
            Expression elseExpr = parseElseExpr();
            caseExpr.setElseExpr(elseExpr);
        }
        checkEndWord();
        return caseExpr;
    }

    private boolean checkIsWhenThenExpr() {
        return checkIsSearchCaseExpr();
    }

    private Expression parseCaseOperandExpr() {
        return parseSingleSelectExpr();
    }

    private WhenThenExpression parseWhenThenExpr(boolean searchFlag) {
        int from = stream.getCursor();
        String whenWord = parseStatementWord();
        if (!SW_WHEN.equalsIgnoreCase(whenWord)) {
            throw stream.createException("Ожидается служебное слово 'WHEN', а получено '" + whenWord + "'", from);
        }
        Expression whenExpr = searchFlag ? parseFullSearchConditionsExpr() : parseSingleSelectExpr();
        from = stream.getCursor();
        String thenWord = parseStatementWord();
        if (!SW_THEN.equalsIgnoreCase(thenWord)) {
            throw stream.createException("Ожидается служебное слово 'THEN', а получено '" + thenWord + "'", from);
        }
        Expression thenExpr = parseSingleSelectExpr();
        WhenThenExpression whenThenExpr = new WhenThenExpression();
        whenThenExpr.setWhenExpr(whenExpr);
        whenThenExpr.setThenExpr(thenExpr);
        return whenThenExpr;
    }

    private boolean checkIsElseExpr() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_ELSE.equalsIgnoreCase(word);
    }

    private Expression parseElseExpr() {
        int from = stream.getCursor();
        String elseWord = parseStatementWord();
        if (!SW_ELSE.equalsIgnoreCase(elseWord)) {
            throw stream.createException("Ожидается служебное слово 'ELSE', а получено '" + elseWord + "'", from);
        }
        Expression elseExpr = parseSingleSelectExpr();
        return elseExpr;
    }

    private void checkEndWord() {
        String endWord = parseStatementWord();
        if (!SW_END.equalsIgnoreCase(endWord)) {
            throw stream.createException("Ожидается слово 'END', а получено '" + endWord + "'", stream.getCursor());
        }
    }

    private boolean checkIsCastExpr() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_CAST.equalsIgnoreCase(word);
    }

    private CastExpression parseCastExpr() {
        int from = stream.getCursor();
        String castWord = parseStatementWord();
        if (!SW_CAST.equalsIgnoreCase(castWord)) {
            throw stream.createException("Ожидается служебное слово 'CAST', а получено '" + castWord + "'", from);
        }
        from = stream.getCursor();
        char parenSymbol = parseDelim(DONT_SQL_WORD_LETTERS, false);
        if ('(' != parenSymbol) {
            throw stream.createException("Ожидается начало CAST функции '(', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        from = stream.getCursor();
        SelectableExpression operandExpr = parseSingleSelectExpr();
        String asWord = parseStatementWord();
        if (!SW_AS.equalsIgnoreCase(asWord)) {
            throw stream.createException("Ожидается служебное слово 'AS' для CAST функции, а получено '" + asWord + "'", from);
        }
        from = stream.getCursor();
        FuncExpression targetExpr = null;
        if (checkIsFuncExpr()) {
            targetExpr = parseFuncExpr();
        } else {
            String typeName = parseStatementWord(false);
            if (typeName.isEmpty()) {
                throw stream.createException("Ожидается имя типа в CAST функции", from);
            }
            targetExpr = new FuncExpression();
            targetExpr.setName(typeName);
        }
        from = stream.getCursor();
        parenSymbol = parseDelim(DONT_SQL_WORD_LETTERS, false);
        if (')' != parenSymbol) {
            throw stream.createException("Ожидается конец CAST функции ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        CastExpression castExpr = new CastExpression();
        castExpr.setOperandExpr(operandExpr);
        castExpr.setTargetExpr(targetExpr);
        return castExpr;
    }

    private boolean checkIsSubSelect() {
        skipSpaces();
        boolean subSelectFlag = false;
        stream.keepParserState();
        char symbol = stream.getSymbol();
        if ('(' == symbol) {
            stream.moveCursor();
            WordInfo wordInfo = parseWordInfo();
            subSelectFlag = wordInfo != null && SW_SELECT.equalsIgnoreCase(wordInfo.getWord())
                    && Character.isWhitespace(wordInfo.getStopSymbol());
        }
        stream.rollbackParserState();
        return subSelectFlag;
    }

    private SelectQuery parseSubSelect() {
        skipSpaces();
        if ('(' != stream.getSymbol()) {
            throw stream.createException("Ожидается начало подзапроса символом '(', а получен '" + stream.getSymbol() + "'", stream
                    .getCursor());
        }
        stream.moveCursor(); // skip '('
        SelectQuery subSelectQuery = parseSingleSelectQuery();
        skipSpaces();
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается заврешение подзапроса символом ')', а получен '" + stream.getSymbol() + "'", stream
                    .getCursor());
        }
        stream.moveCursor(); // skip ')'
        subSelectQuery.setWrapped(true);
        return subSelectQuery;
    }

    private boolean checkIsColumnExpr() {
        return checkIsIdentifier();
    }

    private ColumnExpression parseColumnExpr() {
        skipSpaces();
        int from = stream.getCursor();
        while (EOF != stream.getSymbol() && Character.isJavaIdentifierPart(stream.getSymbol())) {
            stream.moveCursor();
        }
        if (from == stream.getCursor()) {
            throw stream.createException("Не является колонкой", from);
        }
        ColumnExpression columnExpr = new ColumnExpression();
        String prefix = null;
        String columnName = null;
        String value = stream.getPartFrom(from);
        char symbol = stream.getSymbol();
        if ('.' == symbol) {
            stream.moveCursor();
            prefix = value;
            from = stream.getCursor();
            char asteriskSymbol = stream.getSymbol();
            if ('*' == asteriskSymbol) {
                columnName = "*";
                stream.moveCursor();
            } else {
                while (EOF != stream.getSymbol() && Character.isJavaIdentifierPart(stream.getSymbol())) {
                    stream.moveCursor();
                }
                if (from == stream.getCursor()) {
                    throw stream.createException("Не является именем колонки", from);
                }
                columnName = stream.getPartFrom(from);
            }
        } else {
            columnName = value;
        }
        columnExpr.setPrefix(prefix);
        columnExpr.setColumnName(columnName);
        return columnExpr;
    }

    private boolean checkIsSqlWord() {
        stream.keepParserState();
        String word = parseStatementWord();
        boolean flag = isSqlWord(word);
        stream.rollbackParserState();
        return flag;
    }

    private FromStatement parseFromStatement() {
        if (checkIsSqlWord()) {
            String word = parseStatementWord();
            if (!SW_FROM.equalsIgnoreCase(word)) {
                throw stream.createException("Ожидается секция 'FROM', а имеем '" + word + "'", stream.getCursor());
            }
            FromStatement fromStatement = new FromStatement();
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
        if (checkIsSqlWord()) {
            stream.keepParserState();
            String word = parseStatementWord();
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
        if (useJoin && checkIsSqlWord()) {
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
        skipSpaces();
        stream.keepParserState();
        char symbol = stream.getSymbol();
        if ('(' != symbol) {
            stream.rollbackParserState();
            return false;
        }
        stream.moveCursor();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_VALUES.equalsIgnoreCase(word);
    }

    private TableValuesExpression parseTableValuesExpression() {
        skipSpaces();
        int from = stream.getCursor();
        if ('(' != stream.getSymbol()) {
            throw stream.createException("Ожидается начало секции '(VALUES', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        from = stream.getCursor();
        String word = parseStatementWord();
        if (!SW_VALUES.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается секция 'VALUES', а имеем '" + word + "'", from);
        }
        TableValuesExpression tableValuesExpr = new TableValuesExpression();
        from = stream.getCursor();
        ValueListExpression valueExpr = parseValueListExpression();
        tableValuesExpr.addValuesExpr(valueExpr);
        skipSpaces();
        while (',' == stream.getSymbol()) {
            stream.moveCursor();
            valueExpr = parseValueListExpression();
            tableValuesExpr.addValuesExpr(valueExpr);
            skipSpaces();
        }
        from = stream.getCursor();
        if (')' != stream.getSymbol()) {
            throw stream.createException("Ожидается конец секции VALUES ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        from = stream.getCursor();
        stream.keepParserState();
        boolean asWordFlag = false;
        String asWord = parseStatementWord();
        if (!SW_AS.equalsIgnoreCase(asWord)) {
            stream.rollbackParserState();
        } else {
            asWordFlag = true;
            stream.skipParserState();
        }
        from = stream.getCursor();
        FuncExpression templateExpr = null;
        if (checkIsFuncExpr()) {
            templateExpr = parseFuncExpr();
        } else {
            String templateName = parseStatementWord(false);
            if (templateName.isEmpty()) {
                throw stream.createException("Ожидается имя шаблона для VALUES выражения", from);
            }
            templateExpr = new FuncExpression();
            templateExpr.setName(templateName);
        }
        from = stream.getCursor();
        tableValuesExpr.setAsWord(asWordFlag);
        tableValuesExpr.setTemplateExpr(templateExpr);
        return tableValuesExpr;
    }

    private boolean checkIsTable() {
        return checkIsIdentifier();
    }

    private TableExpression parseTableExpression() {
        TableExpression tableExpr = new TableExpression();
        WordInfo wordInfo = parseWordInfo();
        String schemaName = null;
        String tableName = null;
        if ('.' == wordInfo.getStopSymbol()) {
            schemaName = wordInfo.getWord();
            wordInfo = parseWordInfo();
            if (wordInfo == null) {
                throw stream.createException("Ожидается логическое имя таблицы", stream.getCursor());
            }
            tableName = wordInfo.getWord();
        } else {
            tableName = wordInfo.getWord();
        }
        // должны остаться на разделителе
        stream.moveCursor(-1);
        tableExpr.setSchemeName(schemaName);
        tableExpr.setTableName(tableName);
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
            checkOnWord();
            Expression condExpr = parseOnConditionExpr();
            tableJoinExpr.setOnConditionExpr(condExpr);
        }
        return tableJoinExpr;
    }

    private String parseJoinWords() {
        String firstWord = parseStatementWord();
        StringBuilder joinWords = new StringBuilder();
        joinWords.append(firstWord);
        if (startFrom(firstWord, SW_CROSS_JOIN, SW_INNER_JOIN)) {
            WordInfo lastWordHolder = parseWordInfo();
            if (lastWordHolder == null) {
                throw stream.createException("Некорректный синтаксис JOIN связки", stream.getCursor());
            }
            String lastWord = lastWordHolder.getWord();
            if (!"JOIN".equalsIgnoreCase(lastWord)) {
                throw stream
                        .createException("Ожидается слово 'JOIN' после служебного слова '" + joinWords + "', а получено '" + lastWord + "'");
            }
            if (!Character.isWhitespace(lastWordHolder.getStopSymbol())) {
                throw stream.createException("Ожидается пробельный символ после служебного слова '" + joinWords + "', а получен '"
                        + lastWordHolder.getStopSymbol() +
                        "'", stream.getCursor());
            }
            joinWords.append(" ").append(lastWord);
            return joinWords.toString();
        }
        if (startFrom(firstWord, SW_LEFT_OUTER_JOIN, SW_RIGHT_OUTER_JOIN, SW_FULL_OUTER_JOIN)) {
            WordInfo secondWordHolder = parseWordInfo();
            WordInfo lastWordHolder = secondWordHolder;
            if (lastWordHolder == null) {
                throw stream.createException("Некорректный синтаксис JOIN связки", stream.getCursor());
            }
            String secondWord = secondWordHolder.getWord();
            if ("OUTER".equalsIgnoreCase(secondWord)) {
                if (!Character.isWhitespace(secondWordHolder.getStopSymbol())) {
                    throw stream.createException("Ожидается пробельный символ после служебного слова '" + firstWord + "', а получен '"
                            + secondWordHolder
                                    .getStopSymbol() + "'");
                }
                joinWords.append(" ").append(secondWord);
                lastWordHolder = parseWordInfo();
                if (lastWordHolder == null) {
                    throw stream.createException("Некорректный синтаксис JOIN связки", stream.getCursor());
                }
            }
            String lastWord = lastWordHolder.getWord();
            if (!"JOIN".equalsIgnoreCase(lastWord)) {
                throw stream.createException("Ожидается слово 'JOIN' после '" + joinWords + "', а получено '" + lastWord + "'", stream
                        .getCursor());
            }
            if (!Character.isWhitespace(lastWordHolder.getStopSymbol())) {
                throw stream.createException("Ожидается пробельный символ после '" + joinWords + "', а получен '" + lastWordHolder
                        .getStopSymbol()
                        + "'");
            }
            joinWords.append(" ").append(lastWord);
            return joinWords.toString();
        }
        throw stream.createException("Некорректный синтаксис JOIN связки", stream.getCursor());
    }

    private TableJoinTypes resolveJoinType(String joinWords) {
        TableJoinTypes joinType = TableJoinTypes.resolveJoinType(joinWords);
        if (joinType == null) {
            throw stream.createException("Не удалось определить тип 'JOIN' соединения таблиц по параметру '" + joinWords + "'", stream
                    .getCursor());
        }
        return joinType;
    }

    private void checkOnWord() {
        String onWord = parseStatementWord();
        if (!SW_ON.equalsIgnoreCase(onWord)) {
            throw stream.createException("Ожидается слово 'ON' после, а получено '" + onWord + "'", stream.getCursor());
        }
    }

    private Expression parseOnConditionExpr() {
        return parseFullSearchConditionsExpr();
    }

    private boolean checkIsWhereStatement() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_WHERE.equalsIgnoreCase(word);
    }

    private WhereStatement parseWhereStatement() {
        if (checkIsWhereStatement()) {
            String word = parseStatementWord();
            if (!SW_WHERE.equalsIgnoreCase(word)) {
                throw stream.createException("Ожидается секция 'WHERE', а имеем '" + word + "'", stream.getCursor());
            }
            Expression searchExpr = parseFullSearchConditionsExpr();
            WhereStatement whereStatementExpr = new WhereStatement();
            whereStatementExpr.setSearchExpr(searchExpr);
            return whereStatementExpr;
        }
        return null;
    }

    private Expression parseFullSearchConditionsExpr() {
        return parseSearchConditionsExpr();
    }

    private Expression parseSearchConditionsExpr() {
        int from = stream.getCursor();
        boolean expressionState = true;
        ParenLevels levels = new ParenLevels();
        skipSpaces();
        if (!isOpenBrace()) {
            levels.push(new ParenLevel(false));
        }
        while (true) {
            skipSpaces();
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
                    skipNotWord();
                    NotExpression notExpr = new NotExpression();
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

    /**
     * TODO ДОБАВИТЬ разбор выражения (column_name1, column_name2, column_name3) = (value1, value2, value3)
     * @return
     */
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
            // throw
            // stream.createException("Неизвестное выражение для условия поиска предиката внутри <row value constructor>",
            // cursor);
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

    /**
     *
     * @param depthWas
     * @return
     */
    private Expression parseSingleRowValueConstructorExpr(ParenLevels levels) {
        int from = stream.getCursor();
        boolean expressionState = true;
        int fromIndex = levels.isEmpty() ? 0 : levels.peek().size();
        skipSpaces();
        while (true) {
            skipSpaces();
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
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_NOT.equalsIgnoreCase(word);
    }

    private void skipNotWord() {
        int from = stream.getCursor();
        String word = parseStatementWord();
        if (!SW_NOT.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'NOT', а получено '" + word + "'", from);
        }
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

    private boolean checkIsExistsPredicateExpr() {
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !wordInfo.isSqlWord()) {
            stream.rollbackParserState();
            return false;
        }
        if (SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            wordInfo = parseWordInfo();
            if (wordInfo == null || !wordInfo.isSqlWord()) {
                stream.rollbackParserState();
                return false;
            }
        }
        if (!SW_EXISTS.equalsIgnoreCase(wordInfo.getWord())) {
            stream.rollbackParserState();
            return false;
        }
        stream.rollbackParserState();
        return true;
    }

    private ExistsPredicateExpression parseExistsPredicateExpr() {
        WordInfo wordInfo = parseWordInfo();
        int from = stream.getCursor();
        boolean useNotWord = false;
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = stream.getCursor();
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_EXISTS.equalsIgnoreCase(wordInfo.getWord())) {
            throw stream.createException("Ожидается 'EXISTS', а получено '" + (wordInfo == null ? "null" : wordInfo.getWord()) + "'", from);
        }
        stream.moveCursor(-1);
        if (!checkIsSubSelect()) {
            throw stream.createException("Ожидается подзапрос в 'EXISTS' выражении", stream.getCursor());
        }
        SelectQuery subSelectExpr = parseSubSelect();
        ExistsPredicateExpression existsExpr = new ExistsPredicateExpression();
        existsExpr.setUseNotWord(useNotWord);
        existsExpr.setSelectQuery(subSelectExpr);
        return existsExpr;
    }

    private boolean checkIsUniquePredicateExpression() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_UNIQUE.equalsIgnoreCase(word);
    }

    private UniquePredicateExpression parseUniquePredicateExpression() {
        String word = parseStatementWord();
        if (!SW_UNIQUE.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'UNIQUE', а получено '" + word + "'", stream.getCursor());
        }
        SelectQuery subSelectExpr = parseSubSelect();
        UniquePredicateExpression uniquePredicateExpr = new UniquePredicateExpression();
        uniquePredicateExpr.setSubQuery(subSelectExpr);
        return uniquePredicateExpr;
    }

    private boolean checkIsQuantifiedComparisonPredicateExpression() {
        if (!checkIsComparisonOperatorType()) {
            return false;
        }
        stream.keepParserState();
        parseComparisonOperatorType();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_ANY.equalsIgnoreCase(word) || SW_SOME.equalsIgnoreCase(word) || SW_ALL.equalsIgnoreCase(word);
    }

    private QuantifiedComparisonPredicateExpression completeQuantifiedComparisonPredicateExpression(Expression expression) {
        ComparisonOperatorType comparisonOperationType = parseComparisonOperatorType();
        int from = stream.getCursor();
        String quantifierWord = parseStatementWord();
        if (!(SW_ANY.equalsIgnoreCase(quantifierWord) || SW_SOME.equalsIgnoreCase(quantifierWord) || SW_ALL
                .equalsIgnoreCase(quantifierWord))) {
            throw stream
                    .createException("Ожидается одно из служебных слов 'ANY', 'ALL', 'SOME', а получено '" + quantifierWord + "'", from);
        }
        SelectQuery subSelectQuery = parseSubSelect();
        QuantifiedComparisonPredicateExpression quanCompPredicateExpr = new QuantifiedComparisonPredicateExpression();
        quanCompPredicateExpr.setBaseExpr(expression);
        quanCompPredicateExpr.setCompOperatorType(comparisonOperationType);
        quanCompPredicateExpr.setQuantifierWord(quantifierWord);
        quanCompPredicateExpr.setSubSelectQuery(subSelectQuery);
        return quanCompPredicateExpr;
    }

    private boolean checkIsComparisonOperatorType() {
        skipSpaces();
        char symbol = stream.getSymbol();
        return '=' == symbol || '<' == symbol || '>' == symbol || '!' == symbol;
    }

    private ComparisonOperatorType parseComparisonOperatorType() {
        skipSpaces();
        char firstSymbol = stream.getSymbol();
        stream.moveCursor();
        char secondSymbol = stream.getSymbol();
        stream.moveCursor();
        if ('=' == firstSymbol) {
            stream.moveCursor(-1);
            return COT_EQUALS;
        }
        if ('!' == firstSymbol && '=' == secondSymbol) {
            return COT_JAVA_NOT_EQUALS;
        }
        if ('<' == firstSymbol) {
            if ('>' == secondSymbol) {
                return COT_NOT_EQUALS;
            }
            if ('=' == secondSymbol) {
                return COT_LESS_THAN_OR_EQUALS;
            }
            stream.moveCursor(-1);
            return COT_LESS_THAN;
        }
        if ('>' == firstSymbol) {
            if ('=' == secondSymbol) {
                return COT_GREATER_THAN_OR_EQUALS;
            }
            stream.moveCursor(-1);
            return COT_GREATER_THAN;
        }
        throw stream.createException("Ожидается операция сравнения '=' или '<' или '<=' или '>' или '>=' или '<>'", stream.getCursor());
    }

    private boolean checkIsNullablePredicateExpression() {
        skipSpaces();
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        stream.rollbackParserState();
        return wordInfo != null && SW_IS.equalsIgnoreCase(wordInfo.getWord());
    }

    private NullablePredicateExpression completeNullablePredicateExpression(Expression expression) {
        NullablePredicateExpression nullPredicateExpr = new NullablePredicateExpression();
        nullPredicateExpr.setExpression(expression);
        int from = stream.getCursor();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !SW_IS.equalsIgnoreCase(wordInfo.getWord())) {
            throw stream.createException("Ожидается служебное слово 'IS'", from);
        }
        boolean useNotWord = false;
        wordInfo = parseWordInfo();
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = stream.getCursor();
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_NULL.equalsIgnoreCase(wordInfo.getWord())) {
            throw stream.createException("Ожидается служебное слово 'NULL'", from);
        }
        nullPredicateExpr.setUseNotWord(useNotWord);
        if (EOF != stream.getSymbol()) {
            stream.moveCursor(-1);
        }
        return nullPredicateExpr;
    }

    private boolean checkIsInPredicateExpression() {
        stream.keepParserState();
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            word = parseStatementWord();
        }
        boolean inPredicateExprFlag = SW_IN.equalsIgnoreCase(word);
        stream.rollbackParserState();
        return inPredicateExprFlag;
    }

    private InPredicateExpression completeInPredicateExpression(Expression baseExpression) {
        int from = stream.getCursor();
        boolean useNotWord = false;
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            useNotWord = true;
            from = stream.getCursor();
            word = parseStatementWord();
        }
        if (!SW_IN.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'IN'", from);
        }
        Expression predicateValueExpr = parsePredicateValueExpression();
        InPredicateExpression inPredicateExpr = new InPredicateExpression();
        inPredicateExpr.setBaseExpression(baseExpression);
        inPredicateExpr.setUseNotWord(useNotWord);
        inPredicateExpr.setPredicateValueExpr(predicateValueExpr);
        return inPredicateExpr;
    }

    private Expression parsePredicateValueExpression() {
        if (checkIsSubSelect()) {
            return parseSubSelect();
        }
        return parseValueListExpression();
    }

    private ValueListExpression parseValueListExpression() {
        skipSpaces();
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
            char symbol = parseDelim(FUNC_ARGS_DELIMS, true);
            if (')' == symbol) {
                break;
            }
            if (',' != symbol) {
                throw stream.createException("Ожидается разделитель между значениями в блоке ',', а получен '" + symbol + "'", from);
            }
            stream.moveCursor();
            from = stream.getCursor();
            valueExpr = parseSingleSelectExpr();
        }
        skipSpaces();
        from = stream.getCursor();
        if (stream.getSymbol() != ')') {
            throw stream.createException("Ожидается заврешение блока значений ')', а получен '" + stream.getSymbol() + "'", from);
        }
        stream.moveCursor();
        return inValueExpr;
    }

    private boolean checkIsLikePredicateExpression() {
        stream.keepParserState();
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            word = parseStatementWord();
        }
        boolean likePredicateExprFlag = SW_LIKE.equalsIgnoreCase(word);
        stream.rollbackParserState();
        return likePredicateExprFlag;
    }

    private LikePredicateExpression completeLikePredicateExpression(Expression baseExpression) {
        int from = stream.getCursor();
        boolean useNotWord = false;
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            useNotWord = true;
            from = stream.getCursor();
            word = parseStatementWord();
        }
        if (!SW_LIKE.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'LIKE'", from);
        }
        SelectableExpression patternExpr = parseSingleSelectExpr();
        Expression escapeExpr = null;
        if (checkIsEscapeWord()) {
            escapeExpr = parseEscapeExpression();
        }
        LikePredicateExpression likeExpr = new LikePredicateExpression();
        likeExpr.setMatchValueExpr(baseExpression);
        likeExpr.setUseNotWord(useNotWord);
        likeExpr.setPatternExpr(patternExpr);
        likeExpr.setEscapeExpr(escapeExpr);
        return likeExpr;
    }

    private boolean checkIsEscapeWord() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_ESCAPE.equalsIgnoreCase(word);
    }

    private Expression parseEscapeExpression() {
        int from = stream.getCursor();
        String word = parseStatementWord();
        if (!SW_ESCAPE.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается служебное слово 'ESCAPE', а получено '" + word + "'", from);
        }
        return parseSingleSelectExpr();
    }

    private boolean checkIsBetweenPredicateExpression() {
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !wordInfo.isSqlWord()) {
            stream.rollbackParserState();
            return false;
        }
        if (SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            wordInfo = parseWordInfo();
            if (wordInfo == null || !wordInfo.isSqlWord()) {
                stream.rollbackParserState();
                return false;
            }
        }
        if (!SW_BETWEEN.equalsIgnoreCase(wordInfo.getWord())) {
            stream.rollbackParserState();
            return false;
        }
        stream.rollbackParserState();
        return true;
    }

    private BetweenPredicateExpression completeBetweenPredicateExpression(Expression baseExpression) {
        WordInfo wordInfo = parseWordInfo();
        int from = stream.getCursor();
        boolean useNotWord = false;
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = stream.getCursor();
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_BETWEEN.equalsIgnoreCase(wordInfo.getWord())) {
            throw stream
                    .createException("Ожидается 'BETWEEN', а получено '" + (wordInfo == null ? "null" : wordInfo.getWord()) + "'", from);
        }
        stream.moveCursor(-1);
        BetweenPredicateExpression betweenPredicateExpr = new BetweenPredicateExpression();
        betweenPredicateExpr.setBaseExpression(baseExpression);
        betweenPredicateExpr.setUseNotWord(useNotWord);
        Expression leftExpr = parseFullSingleRowValueConstructorExpr();
        betweenPredicateExpr.setLeftExpression(leftExpr);
        from = stream.getCursor();
        ConditionFlowType conditionFlowType = parseConditionFlowType();
        if (ConditionFlowType.CFT_AND != conditionFlowType) {
            throw stream.createException("Ожидается 'AND', а получено '" + (conditionFlowType == null ? "null" : conditionFlowType
                    .getCondition())
                    + "'", from);
        }
        Expression rightExpr = parseFullSingleRowValueConstructorExpr();
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
        skipSpaces();
        stream.keepParserState();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null) {
            stream.rollbackParserState();
            return null;
        }
        if (EOF != wordInfo.getStopSymbol()) {
            stream.moveCursor(-1);
        }
        String conditionWord = wordInfo.getWord();
        ConditionFlowType condType = resolveConditionFlowType(conditionWord);
        if (condType == null) {
            stream.rollbackParserState();
        } else {
            stream.skipParserState();
        }
        return condType;
    }

    private boolean checkIsValuesComparisonPredicateExpression() {
        skipSpaces();
        return ',' == stream.getSymbol();
    }

    private ComparisonPredicateExpression completeValuesComparisonPredicateExpression(Expression firstExpr, ParenLevels levels) {
        int from = stream.getCursor();
        if (levels.isEmpty()) {
            throw stream.createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        ValueListExpression leftValuesExpr = new ValueListExpression();
        leftValuesExpr.addValueExpr(firstExpr);
        skipSpaces();
        while (',' == stream.getSymbol()) {
            stream.moveCursor();
            from = stream.getCursor();
            Expression valueExpr = parseSelectableExpression();
            skipSpaces();
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
        stream.keepParserState();
        String firstWord = parseStatementWord();
        String secondWord = parseStatementWord();
        stream.rollbackParserState();
        return SW_GROUP.equalsIgnoreCase(firstWord) && SW_BY.equalsIgnoreCase(secondWord);
    }

    private GroupByStatement parseGroupByStatement() {
        if (checkIsGroupByStatement()) {
            String firstWord = parseStatementWord();
            String secondWord = parseStatementWord();
            if (!SW_GROUP.equalsIgnoreCase(firstWord) || !SW_BY.equalsIgnoreCase(secondWord)) {
                throw stream.createException("Ожидается секция 'GROUP BY', а имеем '" + firstWord + " " + secondWord + "'", stream
                        .getCursor());
            }
            GroupByStatement groupByStatement = new GroupByStatement();
            GroupingColumnReferenceExpression firstRefColumnExpr = parseGroupingColumnReferenceExpression();
            groupByStatement.addGroupByExpr(firstRefColumnExpr);
            skipSpaces();
            while (',' == stream.getSymbol()) {
                stream.moveCursor();
                GroupingColumnReferenceExpression refColumnExpr = parseGroupingColumnReferenceExpression();
                groupByStatement.addGroupByExpr(refColumnExpr);
                skipSpaces();
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
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_HAVING.equalsIgnoreCase(word);
    }

    private HavingStatement parseHavingStatement() {
        if (checkIsHavingStatement()) {
            String word = parseStatementWord();
            if (!SW_HAVING.equalsIgnoreCase(word)) {
                throw stream.createException("Ожидается секция 'HAVING', а имеем '" + word + "'", stream.getCursor());
            }
            Expression searchExpr = parseFullSearchConditionsExpr();
            HavingStatement havingStatementExpr = new HavingStatement();
            havingStatementExpr.setSearchExpr(searchExpr);
            return havingStatementExpr;
        }
        return null;
    }

    private OrderByStatement parseOrderByStatement() {
        stream.keepParserState();
        String orderWord = parseStatementWord();
        if (!"ORDER".equalsIgnoreCase(orderWord)) {
            stream.rollbackParserState();
            return null;
        }
        stream.skipParserState();
        int from = stream.getCursor();
        String byWord = parseStatementWord();
        if (!SW_BY.equalsIgnoreCase(byWord)) {
            throw stream.createException("Ожидается секция 'ORDER BY', а имеем 'ORDER " + byWord + "'", from);
        }
        from = stream.getCursor();
        OrderByStatement orderByStatement = new OrderByStatement();
        SortKeyExpression firstSortKeyExpr = parseSortKeyExpression();
        orderByStatement.addSortKeyExpr(firstSortKeyExpr);
        while (',' == stream.getSymbol()) {
            stream.moveCursor();
            SortKeyExpression nextSortKeyExpr = parseSortKeyExpression();
            orderByStatement.addSortKeyExpr(nextSortKeyExpr);
        }

        return orderByStatement;
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
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_COLLATE.equalsIgnoreCase(word);
    }

    private CollateExpression parseCollateExpression() {
        int from = stream.getCursor();
        String word = parseStatementWord();
        if (!SW_COLLATE.equalsIgnoreCase(word)) {
            throw stream.createException("Ожидается секция 'COLLATE', а имеем '" + word + "'", from);
        }
        Expression collationNameExpr = parseColumnExpr();
        CollateExpression collateExpr = new CollateExpression();
        collateExpr.setCollationNameExpr(collationNameExpr);

        return collateExpr;
    }

    private boolean checkIsOrderingSpecification() {
        stream.keepParserState();
        String word = parseStatementWord();
        stream.rollbackParserState();
        return SW_ASC.equalsIgnoreCase(word) || SW_DESC.equalsIgnoreCase(word);
    }

    private String parseOrderingSpecification() {
        int from = stream.getCursor();
        String word = parseStatementWord();
        if (!(SW_ASC.equalsIgnoreCase(word) || SW_DESC.equalsIgnoreCase(word))) {
            throw stream.createException("Ожидается служебное слово 'ASC' или 'DESC', а имеем '" + word + "'", from);
        }
        return word;
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

    private LimitExpression parseLimitExpression() {
        stream.keepParserState();
        String limitWord = parseStatementWord();
        if (!SW_LIMIT.equalsIgnoreCase(limitWord)) {
            stream.rollbackParserState();
            return null;
        }
        stream.skipParserState();
        LimitExpression limitExpr = new LimitExpression();
        limitExpr.setLimitWord(limitWord);
        if (checkIsValueExpr()) {
            ValueExpression valueExpr = parseValueExpr();
            limitExpr.setLimitExpr(valueExpr);
        } else if (checkIsAllWord()) {
            String allWord = parseStatementWord();
            KeyWordExpression kwAllExpr = new KeyWordExpression(allWord);
            limitExpr.setLimitExpr(kwAllExpr);
        } else {
            throw stream.createException("Ожидается служебное слово 'ALL' или выражение значения в секции 'LIMIT'", stream.getCursor());
        }
        return limitExpr;
    }

    private OffsetExpression parseOffsetExpression() {
        stream.keepParserState();
        String limitWord = parseStatementWord();
        if (!SW_OFFSET.equalsIgnoreCase(limitWord)) {
            stream.rollbackParserState();
            return null;
        }
        stream.skipParserState();
        OffsetExpression offsetExpr = new OffsetExpression();
        offsetExpr.setOffsetWord(limitWord);
        if (checkIsValueExpr()) {
            ValueExpression valueExpr = parseValueExpr();
            offsetExpr.setOffsetExpr(valueExpr);
        } else {
            throw stream.createException("Ожидается выражение значения в секции 'OFFSET'", stream.getCursor());
        }
        return offsetExpr;
    }

    /**
     *
     * @return в верхнем регистре
     */
    private String parseStatementWord() {
        return parseStatementWord(true);
    }

    private String parseStatementWord(boolean toUpperCase) {
        return parseStatementWord(toUpperCase, DONT_SQL_WORD_LETTERS);
    }

    private String parseStatementWord(boolean toUpperCase, List<Character> dontSqlWordLetters) {
        WordInfo wordInfo = parseWordInfo(dontSqlWordLetters);
        if (wordInfo != null) {
            String word = wordInfo.getWord();
            stream.changeCursor(wordInfo.getAfterWordPos());
            return toUpperCase ? word.toUpperCase() : word;
        }
        return "";
    }

    private static final List<Character> DONT_SQL_WORD_LETTERS = Arrays.asList(
            '.', ':', '(', ')', '!', '?', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0');

    private static final List<Character> DONT_SQL_WORD_LETTERS_EXCLUDE_CLAUSE = initClause();

    private static List<Character> initClause() {
        List<Character> chars = new ArrayList<>(DONT_SQL_WORD_LETTERS);
        chars.remove((Character) '#');
        chars.remove((Character) '.');
        chars.remove((Character) ':');
        return Collections.unmodifiableList(chars);
    }

    private WordInfo parseWordInfo() {
        return parseWordInfo(DONT_SQL_WORD_LETTERS);
    }

    private WordInfo parseWordInfo(List<Character> dontSqlWordLetters) {
        skipSpaces();
        int from = stream.getCursor();
        int afterWordPos = stream.getCursor();
        while (!dontSqlWordLetters.contains(stream.getSymbol())) {
            if (Character.isWhitespace(stream.getSymbol())) {
                int tempPos = stream.getCursor();
                skipSpaces();
                if (dontSqlWordLetters.contains(stream.getSymbol())) {
                    break;
                }
                stream.changeCursor(tempPos);
                break;
            }
            stream.moveCursor();
            afterWordPos = stream.getCursor();
        }
        String word = null;
        if (stream.getCursor() != from) {
            word = stream.getPartFrom(from).trim();
            char delim = stream.getSymbol();
            stream.moveCursor();
            return new WordInfo(word, afterWordPos, delim);
        }
        return null;
    }

    private char parseDelim(List<Character> delims, boolean throwFlag) {
        skipSpaces();
        char symbol = stream.getSymbol();
        if (!delims.contains(symbol)) {
            if (throwFlag) {
                throw stream.createException("Ожидается разделитель один из '" + delims + "', а получен символ '" + symbol + "'");
            }
            symbol = EOF;
        }
        return symbol;
    }

    private boolean checkIsIdentifier() {
        skipSpaces();
        char symbol = stream.getSymbol();
        if (EOF == symbol || !Character.isJavaIdentifierPart(symbol)) {
            return false;
        }
        stream.keepParserState();
        WordInfo holder = parseWordInfo();
        stream.rollbackParserState();
        return holder != null && !holder.isSqlWord();
    }

    private void skipSpaces() {
        while (Character.isWhitespace(stream.getSymbol())) {
            stream.moveCursor();
        }
    }
}

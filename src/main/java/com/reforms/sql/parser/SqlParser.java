package com.reforms.sql.parser;

import static com.reforms.sql.expr.term.ConditionFlowType.resolveConditionFlowType;
import static com.reforms.sql.expr.term.MathOperator.MO_CONCAT;
import static com.reforms.sql.expr.term.MathOperator.resolveMathOperator;
import static com.reforms.sql.expr.term.SqlWords.*;
import static com.reforms.sql.expr.term.from.TableJoinTypes.TJT_CROSS_JOIN;
import static com.reforms.sql.expr.term.predicate.ComparisonOperatorType.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import com.reforms.sql.expr.query.LinkingSelectQuery;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.*;
import com.reforms.sql.expr.term.casee.CaseExpression;
import com.reforms.sql.expr.term.casee.WhenThenExpression;
import com.reforms.sql.expr.term.from.*;
import com.reforms.sql.expr.term.predicate.*;
import com.reforms.sql.expr.term.value.*;

/**
 * 1. ORACLE: SELECT selection_fields FROM (SELECT selection_fields,ROWNUM RN FROM (SELECT selection_fields FROM schemaName.tableName WHERE conditions ORDER BY orderFields)) WHERE RN > ? AND RN <= ?
 * 2. MSSQL: SELECT TOP 50 id, name FROM schemaName.tableName WHERE name LIKE ? AND __id__ NOT IN (SELECT TOP 100 __id__ FROM schemaName.tableName WHERE name LIKE ? ORDER BY id) ORDER BY id
 * @author palihov
 */
public class SqlParser {

    private static final char EOL = '\n';

    private static final char EOF = '\0';

    private String query;

    private int cursor;

    private int lineNumber;

    private Deque<Marker> markers = new ArrayDeque<>();

    public SqlParser(String query) {
        this.query = query;
    }

    public SelectQuery parseSelectQuery() {
        SelectQuery directSelectQuery = parseSingleSelectQuery();
        if (cursor < query.length()) {
            throw createException("Не удалось до конца разобрать запрос", cursor);
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
        while (isLinkedWord()) {
            LinkingSelectQuery linkedSelectExpr = new LinkingSelectQuery();
            String linkedWord = parseStatementWord();
            linkedSelectExpr.setLinkedWord(linkedWord);
            if (isAllWord()) {
                String allWord = parseAllWord();
                linkedSelectExpr.setAllWord(allWord);
            }
            SelectQuery linkedSelectQuery = parseSingleSelectQuery();
            linkedSelectExpr.setLinkedSelectQuery(linkedSelectQuery);
            selectQuery.addLinkingQuery(linkedSelectExpr);
        }
        OrderByStatement orderByStatement = parseOrderByStatement();
        selectQuery.setOrderByStatement(orderByStatement);
        return selectQuery;
    }

    private boolean isLinkedWord() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_UNION.equalsIgnoreCase(word) || SW_EXCEPT.equalsIgnoreCase(word)
                || SW_INTERSECT.equalsIgnoreCase(word) || SW_MINUS.equalsIgnoreCase(word);
    }

    private boolean isAllWord() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_ALL.equalsIgnoreCase(word);
    }

    private String parseAllWord() {
        int from = cursor;
        String word = parseStatementWord();
        if (!SW_ALL.equalsIgnoreCase(word)) {
            throw createException("Ожидается ключевое слово 'ALL', а получено '" + word + "'", from);
        }
        return word;
    }

    private void fillFullSelectStatement(SelectQuery selectQuery) {
        // 1. SELECT word
        int from = cursor;
        String selectWord = parseStatementWord();
        if (!SW_SELECT.equalsIgnoreCase(selectWord)) {
            throw createException("Ожидается ключевое слово 'SELECT', а получено '" + selectWord + "'", from);
        }
        // 2. [ALL | DISTINCT] mode word
        String selectModeWord = parseSelectModeWord();
        // 3. SELECT STATEMENT
        SelectStatement selectStatement = parseSelectStatement();
        selectStatement.setModeWord(selectModeWord);
        selectQuery.setSelectStatement(selectStatement);
    }

    private String parseSelectModeWord() {
        keep();
        String modeWord = parseStatementWord();
        if (SW_ALL.equalsIgnoreCase(modeWord) || SW_DISTINCT.equalsIgnoreCase(modeWord)) {
            popupKeep();
            return modeWord;
        } else {
            rollback();
        }
        return null;
    }

    private SelectStatement parseSelectStatement() {
        SelectStatement selectStatement = new SelectStatement();
        SelectableExpression selectExpr = parseFullSelectableExpression();
        if (selectExpr == null) {
            throw createException("В выражении SELECT должен быть хотя бы 1 параметр выборки", cursor);
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
                moveCursor();
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
            AsClauseExpression asClauseExpr = parseAsClauseExpression();
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
        int from = cursor;
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
                moveCursor();
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
                    throw createException("Ошибка при разборе выборки. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                moveCursor();
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
            throw createException("Ошибка при разборе выборки. Количество закрывающих скобок ')' меньше, чем открывающихся '('", from);
        }
        if (levels.size() != 1) {
            throw createException("Ошибка при разборе выборки. Количество вложений '" + levels.size() + "'", from);
        }
        ParenLevel rootLevel = levels.pop();
        SelectableExpression searchExprs = (SelectableExpression) rootLevel.combine(false);
        return searchExprs;
    }

    private static final List<Character> MATH_OPERAND = Arrays.asList('+', '-', '*', '/', '|');

    private boolean checkIsMathOperator() {
        skipSpaces();
        char symbol = getSymbol();
        return MATH_OPERAND.contains(symbol);
    }

    private MathOperator parseMathOperator() {
        skipSpaces();
        char symbol = getSymbol();
        if (!MATH_OPERAND.contains(symbol)) {
            throw createException("Некорректный математический оператор '" + symbol + "'", cursor);
        }
        moveCursor();
        if ('|' != symbol) {
            return resolveMathOperator("" + symbol);
        }
        char secondSymbol = getSymbol();
        if ('|' != secondSymbol) {
            throw createException("Некорректный оператор конкатинации '" + secondSymbol + "'", cursor);
        }
        moveCursor();
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
        throw createException("Неизвестное выражение для выборки", cursor);
    }

    private boolean checkIsAsterisk() {
        skipSpaces();
        char symbol = getSymbol();
        return '*' == symbol;
    }

    private AsteriskExpression parseAsterisk() {
        AsteriskExpression asteriskExpr = new AsteriskExpression();
        moveCursor();
        return asteriskExpr;
    }

    private boolean checkIsFuncExpr() {
        keep();
        WordInfo wordInfo = parseWordInfo();
        boolean funcFlag = wordInfo != null && !wordInfo.isSqlWord() && '(' == wordInfo.getStopSymbol();
        rollback();
        return funcFlag;
    }

    private static final List<Character> FUNC_ARGS_DELIMS = Arrays.asList(',', ')');

    private FuncExpression parseFuncExpr() {
        FuncExpression funcExpr = new FuncExpression();
        int from = cursor;
        String funcName = parseStatementWord(false);
        if (funcName.isEmpty()) {
            throw createException("Ожидается наименование функции", from);
        }
        funcExpr.setName(funcName);
        skipSpaces();
        from = cursor;
        char symol = getSymbol();
        if ('(' != symol) {
            throw createException("Ожидается начало функции '(', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        String quantifier = parseSelectModeWord();
        funcExpr.setQuantifier(quantifier);
        from = cursor;
        skipSpaces();
        symol = getSymbol();
        if (')' != symol) {
            from = cursor;
            SelectableExpression arg = parseSingleSelectExpr();
            while (true) {
                funcExpr.addArg(arg);
                char symbol = parseDelim(FUNC_ARGS_DELIMS, true);
                if (')' == symbol) {
                    break;
                }
                if (',' != symbol) {
                    throw createException("Ожидается разделитель между аргументами функции ',', а получен '" + symbol + "'", from);
                }
                moveCursor();
                from = cursor;
                arg = parseSingleSelectExpr();
            }
            skipSpaces();
        }
        if (')' != getSymbol()) {
            throw createException("Ожидается заврешение функции символом ')', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        return funcExpr;
    }

    private AsClauseExpression parseAsClauseExpression() {
        skipSpaces();
        keep();
        int from = cursor;
        AsClauseExpression asClauseExpr = new AsClauseExpression();
        String word = parseStatementWord(false);
        if (word.isEmpty()) {
            word = parseDoubleQuoteValue();
        }
        if (SW_AS.equalsIgnoreCase(word)) {
            asClauseExpr.setAsWord(word);
            from = cursor;
            word = parseStatementWord(false);
            if (word.isEmpty()) {
                word = parseDoubleQuoteValue();
                if (word.isEmpty()) {
                    throw createException("После ключеого слова 'AS' ожидается алиас", from);
                }
            }
            popupKeep();
            asClauseExpr.setAlias(word);
            return asClauseExpr;
        } else if (!isSqlWord(word) && !word.isEmpty()) {
            popupKeep();
            asClauseExpr.setAlias(word);
            return asClauseExpr;
        }
        rollback();
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
        char symbol = getSymbol();
        return symbol == '-' || symbol == '+' || Character.isDigit(symbol);
    }

    private NumericExpression parseNumericExpr() {
        skipSpaces();
        int from = cursor;
        boolean wasDot = false;
        boolean wasE = false;
        while (true) {
            char symbol = getSymbol();
            if (('+' == symbol || '-' == symbol) && from == cursor) {
                moveCursor();
                continue;
            }
            if (Character.isDigit(symbol)) {
                moveCursor();
                continue;
            }
            if ('.' == symbol && !(wasDot || wasE)) {
                wasDot = true;
                moveCursor();
                continue;
            }
            if ('E' == symbol && !wasE) {
                moveCursor();
                char signSymbol = getSymbol();
                if ('+' == signSymbol || '-' == signSymbol) {
                    moveCursor();
                    char digitSymbol = getSymbol();
                    if (Character.isDigit(digitSymbol)) {
                        wasE = true;
                        continue;
                    }
                    moveCursor(-1);
                }
                moveCursor(-1);
            }
            break;
        }
        if (from == cursor) {
            throw createException("Не является числом", cursor);
        }
        char prevSymbol = getSymvol(-1);
        if (cursor - from == 1 && ('+' == prevSymbol || '-' == prevSymbol)) {
            throw createException("Ожидается после знака '+' или '-' хотя бы 1 число!", from);
        }
        String numericValue = query.substring(from, cursor);
        return new NumericExpression(numericValue);
    }

    private boolean checkIsStringExpr() {
        skipSpaces();
        char symbol = getSymbol();
        return '\'' == symbol;
    }

    private StringExpression parseStringExpr() {
        skipSpaces();
        int from = cursor;
        char symbol = getSymbol();
        if ('\'' == symbol) {
            moveCursor();
            while ('\'' != (symbol = getSymbol()) && symbol != '\0') {
                moveCursor();
            }
        }
        if (symbol == '\0') {
            throw createException("Не является строкой", from);
        }
        moveCursor();
        String stringValue = query.substring(from, cursor);
        return new StringExpression(stringValue);
    }

    private String parseDoubleQuoteValue() {
        skipSpaces();
        int from = cursor;
        char symbol = getSymbol();
        if ('"' != symbol) {
            return "";
        }
        moveCursor();
        while ('"' != (symbol = getSymbol()) && symbol != '\0') {
            moveCursor();
        }
        if (symbol == '\0') {
            throw createException("Не является строкой в двойных кавычках", from);
        }
        moveCursor();
        String doubleQuoteValue = query.substring(from, cursor);
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
        keep();
        WordInfo wordInfo = parseWordInfo();
        rollback();
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
        throw createException("Ожидается 'NULL' или 'TRUE' или 'FALSE', а получено '" + word + "'", cursor);
    }

    private boolean checkIsQuestionExpr() {
        skipSpaces();
        char symbol = getSymbol();
        return symbol == '?';
    }

    private QuestionExpression parseQuestionExpr() {
        skipSpaces();
        char symbol = getSymbol();
        if ('?' != symbol) {
            throw createException("Ожидается '?', а получен символ '" + symbol + "'", cursor);
        }
        moveCursor();
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
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_CASE.equalsIgnoreCase(word);
    }

    private CaseExpression parseCaseExpr() {
        String word = parseStatementWord();
        int from = cursor;
        if (!SW_CASE.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'CASE', а получено '" + word + "'", from);
        }
        boolean searchFlag = checkIsSearchCaseExpr();
        return parseDirectCaseExpression(searchFlag);
    }

    private boolean checkIsFilterExpr() {
        skipSpaces();
        return ':' == getSymbol();
    }

    private FilterExpression parseFilterExpr() {
        int from = cursor;
        if (!checkIsFilterExpr()) {
            throw createException("Ожидается символ ':', а получен '" + getSymbol() + "' в выражении типа фильтр", from);
        }
        int startExprCursor = cursor;
        while (':' == getSymbol()) {
            moveCursor();
        }
        from = cursor;
        int filterNameCursor = cursor;
        while (EOF != getSymbol() &&
                (Character.isJavaIdentifierPart(getSymbol()) || '.' == getSymbol())) {
            moveCursor();
        }
        if (filterNameCursor == cursor) {
            throw createException("Не является фильтром", from);
        }
        from = cursor;
        String filterName = query.substring(filterNameCursor, cursor);
        boolean questionFlag = false;
        if ('?' == getSymbol()) {
            questionFlag = true;
            moveCursor();
        }
        String filterValue = query.substring(startExprCursor, cursor);
        FilterExpression filterExpr = new FilterExpression(filterValue);
        filterExpr.setColonCount(filterNameCursor - startExprCursor);
        filterExpr.setFilterName(filterName);
        filterExpr.setQuestionFlag(questionFlag);
        return filterExpr;
    }

    private boolean checkIsSearchCaseExpr() {
        keep();
        String word = parseStatementWord();
        rollback();
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
        int from = cursor;
        String whenWord = parseStatementWord();
        if (!SW_WHEN.equalsIgnoreCase(whenWord)) {
            throw createException("Ожидается служебное слово 'WHEN', а получено '" + whenWord + "'", from);
        }
        Expression whenExpr = searchFlag ? parseFullSearchConditionsExpr() : parseSingleSelectExpr();
        from = cursor;
        String thenWord = parseStatementWord();
        if (!SW_THEN.equalsIgnoreCase(thenWord)) {
            throw createException("Ожидается служебное слово 'THEN', а получено '" + thenWord + "'", from);
        }
        Expression thenExpr = parseSingleSelectExpr();
        WhenThenExpression whenThenExpr = new WhenThenExpression();
        whenThenExpr.setWhenExpr(whenExpr);
        whenThenExpr.setThenExpr(thenExpr);
        return whenThenExpr;
    }

    private boolean checkIsElseExpr() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_ELSE.equalsIgnoreCase(word);
    }

    private Expression parseElseExpr() {
        int from = cursor;
        String elseWord = parseStatementWord();
        if (!SW_ELSE.equalsIgnoreCase(elseWord)) {
            throw createException("Ожидается служебное слово 'ELSE', а получено '" + elseWord + "'", from);
        }
        Expression elseExpr = parseSingleSelectExpr();
        return elseExpr;
    }

    private void checkEndWord() {
        String endWord = parseStatementWord();
        if (!SW_END.equalsIgnoreCase(endWord)) {
            throw createException("Ожидается слово 'END', а получено '" + endWord + "'", cursor);
        }
    }

    private boolean checkIsCastExpr() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_CAST.equalsIgnoreCase(word);
    }

    private CastExpression parseCastExpr() {
        int from = cursor;
        String castWord = parseStatementWord();
        if (!SW_CAST.equalsIgnoreCase(castWord)) {
            throw createException("Ожидается служебное слово 'CAST', а получено '" + castWord + "'", from);
        }
        from = cursor;
        char parenSymbol = parseDelim(DONT_SQL_WORD_LETTERS, false);
        if ('(' != parenSymbol) {
            throw createException("Ожидается начало CAST функции '(', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        from = cursor;
        SelectableExpression operandExpr = parseSingleSelectExpr();
        String asWord = parseStatementWord();
        if (!SW_AS.equalsIgnoreCase(asWord)) {
            throw createException("Ожидается служебное слово 'AS' для CAST функции, а получено '" + asWord + "'", from);
        }
        from = cursor;
        FuncExpression targetExpr = null;
        if (checkIsFuncExpr()) {
            targetExpr = parseFuncExpr();
        } else {
            String typeName = parseStatementWord(false);
            if (typeName.isEmpty()) {
                throw createException("Ожидается имя типа в CAST функции", from);
            }
            targetExpr = new FuncExpression();
            targetExpr.setName(typeName);
        }
        from = cursor;
        parenSymbol = parseDelim(DONT_SQL_WORD_LETTERS, false);
        if (')' != parenSymbol) {
            throw createException("Ожидается конец CAST функции ')', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        CastExpression castExpr = new CastExpression();
        castExpr.setOperandExpr(operandExpr);
        castExpr.setTargetExpr(targetExpr);
        return castExpr;
    }

    private boolean checkIsSubSelect() {
        skipSpaces();
        boolean subSelectFlag = false;
        keep();
        char symbol = getSymbol();
        if ('(' == symbol) {
            moveCursor();
            WordInfo wordInfo = parseWordInfo();
            subSelectFlag = wordInfo != null && SW_SELECT.equalsIgnoreCase(wordInfo.getWord())
                    && Character.isWhitespace(wordInfo.getStopSymbol());
        }
        rollback();
        return subSelectFlag;
    }

    private SelectQuery parseSubSelect() {
        skipSpaces();
        if ('(' != getSymbol()) {
            throw createException("Ожидается начало подзапроса символом '(', а получен '" + getSymbol() + "'", cursor);
        }
        moveCursor(); // skip '('
        SelectQuery subSelectQuery = parseSingleSelectQuery();
        skipSpaces();
        if (')' != getSymbol()) {
            throw createException("Ожидается заврешение подзапроса символом ')', а получен '" + getSymbol() + "'", cursor);
        }
        moveCursor(); // skip ')'
        subSelectQuery.setWrapped(true);
        return subSelectQuery;
    }

    private boolean checkIsColumnExpr() {
        return checkIsIdentifier();
    }

    private ColumnExpression parseColumnExpr() {
        skipSpaces();
        int from = cursor;
        while (EOF != getSymbol() && Character.isJavaIdentifierPart(getSymbol())) {
            moveCursor();
        }
        if (from == cursor) {
            throw createException("Не является колонкой", from);
        }
        ColumnExpression columnExpr = new ColumnExpression();
        String prefix = null;
        String columnName = null;
        String value = query.substring(from, cursor);
        char symbol = getSymbol();
        if ('.' == symbol) {
            moveCursor();
            prefix = value;
            from = cursor;
            char asteriskSymbol = getSymbol();
            if ('*' == asteriskSymbol) {
                columnName = "*";
                moveCursor();
            } else {
                while (EOF != getSymbol() && Character.isJavaIdentifierPart(getSymbol())) {
                    moveCursor();
                }
                if (from == cursor) {
                    throw createException("Не является именем колонки", from);
                }
                columnName = query.substring(from, cursor);
            }
        } else {
            columnName = value;
        }
        columnExpr.setPrefix(prefix);
        columnExpr.setColumnName(columnName);
        return columnExpr;
    }

    private boolean checkIsSqlWord() {
        keep();
        String word = parseStatementWord();
        boolean flag = isSqlWord(word);
        rollback();
        return flag;
    }

    private FromStatement parseFromStatement() {
        if (checkIsSqlWord()) {
            String word = parseStatementWord();
            if (!SW_FROM.equalsIgnoreCase(word)) {
                throw createException("Ожидается секция 'FROM', а имеем '" + word + "'", cursor);
            }
            FromStatement fromStatement = new FromStatement();
            List<TableReferenceExpression> tableRefExprs = fromStatement.getTableRefExprs();
            while (checkIsTableReference(!tableRefExprs.isEmpty())) {
                TableReferenceExpression tableRefExpr = parseTableReference(!tableRefExprs.isEmpty());
                fromStatement.addTableRefExpr(tableRefExpr);
                if (',' == getSymbol()) {
                    tableRefExpr.setSeparator(", ");
                    moveCursor();
                } else if (tableRefExprs.size() > 1) {
                    int prevIndex = tableRefExprs.size() - 2;
                    TableReferenceExpression prevTableRefExpr = tableRefExprs.get(prevIndex);
                    if (prevTableRefExpr.getSeparator() == null) {
                        prevTableRefExpr.setSeparator(" ");
                    }
                }
            }
            if (tableRefExprs.isEmpty()) {
                throw createException("Ожидается в секции 'FROM' блок данных о таблицах", cursor);
            }
            return fromStatement;
        }
        return null;
    }

    private boolean checkIsTableReference(boolean useJoin) {
        if (checkIsSqlWord()) {
            keep();
            String word = parseStatementWord();
            rollback();
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
        throw createException("Неизвестный тип данных о таблицах", cursor);
    }

    private TableReferenceExpression parseTableSubQuery() {
        SelectQuery selectQuery = parseSubSelect();
        TableSubQueryExpression tableSubQuery = new TableSubQueryExpression();
        tableSubQuery.setSubQueryExpr(selectQuery);
        tableSubQuery.setAsClauseExpr(parseAsClauseExpression());
        return tableSubQuery;
    }

    private boolean checkIsTableValuesExpression() {
        skipSpaces();
        keep();
        char symbol = getSymbol();
        if ('(' != symbol) {
            rollback();
            return false;
        }
        moveCursor();
        String word = parseStatementWord();
        rollback();
        return SW_VALUES.equalsIgnoreCase(word);
    }

    private TableValuesExpression parseTableValuesExpression() {
        skipSpaces();
        int from = cursor;
        if ('(' != getSymbol()) {
            throw createException("Ожидается начало секции '(VALUES', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        from = cursor;
        String word = parseStatementWord();
        if (!SW_VALUES.equalsIgnoreCase(word)) {
            throw createException("Ожидается секция 'VALUES', а имеем '" + word + "'", from);
        }
        TableValuesExpression tableValuesExpr = new TableValuesExpression();
        from = cursor;
        ValueListExpression valueExpr = parseValueListExpression();
        tableValuesExpr.addValuesExpr(valueExpr);
        skipSpaces();
        while (',' == getSymbol()) {
            moveCursor();
            valueExpr = parseValueListExpression();
            tableValuesExpr.addValuesExpr(valueExpr);
            skipSpaces();
        }
        from = cursor;
        if (')' != getSymbol()) {
            throw createException("Ожидается конец секции VALUES ')', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        from = cursor;
        keep();
        boolean asWordFlag = false;
        String asWord = parseStatementWord();
        if (!SW_AS.equalsIgnoreCase(asWord)) {
            rollback();
        } else {
            asWordFlag = true;
            popupKeep();
        }
        from = cursor;
        FuncExpression templateExpr = null;
        if (checkIsFuncExpr()) {
            templateExpr = parseFuncExpr();
        } else {
            String templateName = parseStatementWord(false);
            if (templateName.isEmpty()) {
                throw createException("Ожидается имя шаблона для VALUES выражения", from);
            }
            templateExpr = new FuncExpression();
            templateExpr.setName(templateName);
        }
        from = cursor;
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
                throw createException("Ожидается логическое имя таблицы", cursor);
            }
            tableName = wordInfo.getWord();
        } else {
            tableName = wordInfo.getWord();
        }
        // должны остаться на разделителе
        moveCursor(-1);
        tableExpr.setSchemaName(schemaName);
        tableExpr.setTableName(tableName);
        tableExpr.setAsClauseExpr(parseAsClauseExpression());
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
                throw createException("Некорректный синтаксис JOIN связки", cursor);
            }
            String lastWord = lastWordHolder.getWord();
            if (!"JOIN".equalsIgnoreCase(lastWord)) {
                throw createException("Ожидается слово 'JOIN' после служебного слова '" + joinWords + "', а получено '" + lastWord + "'",
                        cursor);
            }
            if (!Character.isWhitespace(lastWordHolder.getStopSymbol())) {
                throw createException("Ожидается пробельный символ после служебного слова '" + joinWords + "', а получен '"
                        + lastWordHolder.getStopSymbol() +
                        "'", cursor);
            }
            joinWords.append(" ").append(lastWord);
            return joinWords.toString();
        }
        if (startFrom(firstWord, SW_LEFT_OUTER_JOIN, SW_RIGHT_OUTER_JOIN, SW_FULL_OUTER_JOIN)) {
            WordInfo secondWordHolder = parseWordInfo();
            WordInfo lastWordHolder = secondWordHolder;
            if (lastWordHolder == null) {
                throw createException("Некорректный синтаксис JOIN связки", cursor);
            }
            String secondWord = secondWordHolder.getWord();
            if ("OUTER".equalsIgnoreCase(secondWord)) {
                if (!Character.isWhitespace(secondWordHolder.getStopSymbol())) {
                    throw createException("Ожидается пробельный символ после служебного слова '" + firstWord + "', а получен '"
                            + secondWordHolder
                                    .getStopSymbol() + "'",
                            cursor);
                }
                joinWords.append(" ").append(secondWord);
                lastWordHolder = parseWordInfo();
                if (lastWordHolder == null) {
                    throw createException("Некорректный синтаксис JOIN связки", cursor);
                }
            }
            String lastWord = lastWordHolder.getWord();
            if (!"JOIN".equalsIgnoreCase(lastWord)) {
                throw createException("Ожидается слово 'JOIN' после '" + joinWords + "', а получено '" + lastWord + "'", cursor);
            }
            if (!Character.isWhitespace(lastWordHolder.getStopSymbol())) {
                throw createException("Ожидается пробельный символ после '" + joinWords + "', а получен '" + lastWordHolder.getStopSymbol()
                        + "'",
                        cursor);
            }
            joinWords.append(" ").append(lastWord);
            return joinWords.toString();
        }
        throw createException("Некорректный синтаксис JOIN связки", cursor);
    }

    private TableJoinTypes resolveJoinType(String joinWords) {
        TableJoinTypes joinType = TableJoinTypes.resolveJoinType(joinWords);
        if (joinType == null) {
            throw createException("Не удалось определить тип 'JOIN' соединения таблиц по параметру '" + joinWords + "'", cursor);
        }
        return joinType;
    }

    private void checkOnWord() {
        String onWord = parseStatementWord();
        if (!SW_ON.equalsIgnoreCase(onWord)) {
            throw createException("Ожидается слово 'ON' после, а получено '" + onWord + "'", cursor);
        }
    }

    private Expression parseOnConditionExpr() {
        return parseFullSearchConditionsExpr();
    }

    private boolean checkIsWhereStatement() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_WHERE.equalsIgnoreCase(word);
    }

    private WhereStatement parseWhereStatement() {
        if (checkIsWhereStatement()) {
            String word = parseStatementWord();
            if (!SW_WHERE.equalsIgnoreCase(word)) {
                throw createException("Ожидается секция 'WHERE', а имеем '" + word + "'", cursor);
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
        int from = cursor;
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
                moveCursor();
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
                    throw createException("Ошибка при разборе условий. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                moveCursor();
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
                from = cursor;
                if (checkIsNotWord()) {
                    skipNotWord();
                    NotExpression notExpr = new NotExpression();
                    ParenLevel currentLevel = levels.peek();
                    currentLevel.add(notExpr);
                    continue;
                }
                from = cursor;
                Expression expr = parseSearchConditionExpr(levels);

                ParenLevel currentLevel = levels.peek();
                if (currentLevel == null) {
                    currentLevel = new ParenLevel(false);
                    levels.push(currentLevel);
                    // throw createException("Ошибка при разборе условий", from);
                }
                currentLevel.add(expr);
                expressionState = false;
                continue;
            }
            from = cursor;
            ConditionFlowType condType = parseConditionFlowType();
            if (condType == null) {
                break;
            }
            ParenLevel currentLevel = levels.peek();
            currentLevel.add(condType);
            expressionState = true;
        }
        if (levels.getDepth() != 0) {
            throw createException("Ошибка при разборе условий. Количество закрывающих скобок ')' меньше, чем открывающихся '('. depth = "
                    + levels.getDepth(),
                    from);
        }
        if (levels.size() != 1) {
            throw createException("Ошибка при разборе условий. Количество вложений '" + levels.size() + "'", from);
        }
        ParenLevel rootLevel = levels.pop();
        Expression searchExprs = rootLevel.combine(false);
        return searchExprs;
    }

    private boolean isOpenBrace() {
        char symbol = getSymbol();
        return '(' == symbol && !checkIsSubSelect();
    }

    private boolean isCloseBrace() {
        char symbol = getSymbol();
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
            // createException("Неизвестное выражение для условия поиска предиката внутри <row value constructor>",
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
        throw createException("Неизвестное выражение для условия поиска", cursor);
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
        int from = cursor;
        boolean expressionState = true;
        int fromIndex = levels.isEmpty() ? 0 : levels.peek().size();
        skipSpaces();
        while (true) {
            skipSpaces();
            if (isOpenBrace()) {
                levels.incDepth();
                levels.push(new ParenLevel(true));
                moveCursor();
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
                    throw createException("Ошибка при разборе выборки. Количество закрывающих скобок ')' больше, чем открывающихся '('",
                            from);
                }
                moveCursor();
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
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_NOT.equalsIgnoreCase(word);
    }

    private void skipNotWord() {
        int from = cursor;
        String word = parseStatementWord();
        if (!SW_NOT.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'NOT', а получено '" + word + "'", from);
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
        throw createException("Неизвестное выражение для условия поиска <row value constructor>", cursor);
    }

    private boolean checkIsExistsPredicateExpr() {
        keep();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !wordInfo.isSqlWord()) {
            rollback();
            return false;
        }
        if (SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            wordInfo = parseWordInfo();
            if (wordInfo == null || !wordInfo.isSqlWord()) {
                rollback();
                return false;
            }
        }
        if (!SW_EXISTS.equalsIgnoreCase(wordInfo.getWord())) {
            rollback();
            return false;
        }
        rollback();
        return true;
    }

    private ExistsPredicateExpression parseExistsPredicateExpr() {
        WordInfo wordInfo = parseWordInfo();
        int from = cursor;
        boolean useNotWord = false;
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = cursor;
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_EXISTS.equalsIgnoreCase(wordInfo.getWord())) {
            throw createException("Ожидается 'EXISTS', а получено '" + (wordInfo == null ? "null" : wordInfo.getWord()) + "'", from);
        }
        moveCursor(-1);
        if (!checkIsSubSelect()) {
            throw createException("Ожидается подзапрос в 'EXISTS' выражении", cursor);
        }
        SelectQuery subSelectExpr = parseSubSelect();
        ExistsPredicateExpression existsExpr = new ExistsPredicateExpression();
        existsExpr.setUseNotWord(useNotWord);
        existsExpr.setSelectQuery(subSelectExpr);
        return existsExpr;
    }

    private boolean checkIsUniquePredicateExpression() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_UNIQUE.equalsIgnoreCase(word);
    }

    private UniquePredicateExpression parseUniquePredicateExpression() {
        String word = parseStatementWord();
        if (!SW_UNIQUE.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'UNIQUE', а получено '" + word + "'", cursor);
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
        keep();
        parseComparisonOperatorType();
        String word = parseStatementWord();
        rollback();
        return SW_ANY.equalsIgnoreCase(word) || SW_SOME.equalsIgnoreCase(word) || SW_ALL.equalsIgnoreCase(word);
    }

    private QuantifiedComparisonPredicateExpression completeQuantifiedComparisonPredicateExpression(Expression expression) {
        ComparisonOperatorType comparisonOperationType = parseComparisonOperatorType();
        int from = cursor;
        String quantifierWord = parseStatementWord();
        if (!(SW_ANY.equalsIgnoreCase(quantifierWord) || SW_SOME.equalsIgnoreCase(quantifierWord) || SW_ALL
                .equalsIgnoreCase(quantifierWord))) {
            throw createException("Ожидается одно из служебных слов 'ANY', 'ALL', 'SOME', а получено '" + quantifierWord + "'", from);
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
        char symbol = getSymbol();
        return '=' == symbol || '<' == symbol || '>' == symbol || '!' == symbol;
    }

    private ComparisonOperatorType parseComparisonOperatorType() {
        skipSpaces();
        char firstSymbol = getSymbol();
        moveCursor();
        char secondSymbol = getSymbol();
        moveCursor();
        if ('=' == firstSymbol) {
            moveCursor(-1);
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
            moveCursor(-1);
            return COT_LESS_THAN;
        }
        if ('>' == firstSymbol) {
            if ('=' == secondSymbol) {
                return COT_GREATER_THAN_OR_EQUALS;
            }
            moveCursor(-1);
            return COT_GREATER_THAN;
        }
        throw createException("Ожидается операция сравнения '=' или '<' или '<=' или '>' или '>=' или '<>'", cursor);
    }

    private boolean checkIsNullablePredicateExpression() {
        skipSpaces();
        keep();
        WordInfo wordInfo = parseWordInfo();
        rollback();
        return wordInfo != null && SW_IS.equalsIgnoreCase(wordInfo.getWord());
    }

    private NullablePredicateExpression completeNullablePredicateExpression(Expression expression) {
        NullablePredicateExpression nullPredicateExpr = new NullablePredicateExpression();
        nullPredicateExpr.setExpression(expression);
        int from = cursor;
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !SW_IS.equalsIgnoreCase(wordInfo.getWord())) {
            throw createException("Ожидается служебное слово 'IS'", from);
        }
        boolean useNotWord = false;
        wordInfo = parseWordInfo();
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = cursor;
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_NULL.equalsIgnoreCase(wordInfo.getWord())) {
            throw createException("Ожидается служебное слово 'NULL'", from);
        }
        nullPredicateExpr.setUseNotWord(useNotWord);
        if (EOF != getSymbol()) {
            moveCursor(-1);
        }
        return nullPredicateExpr;
    }

    private boolean checkIsInPredicateExpression() {
        keep();
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            word = parseStatementWord();
        }
        boolean inPredicateExprFlag = SW_IN.equalsIgnoreCase(word);
        rollback();
        return inPredicateExprFlag;
    }

    private InPredicateExpression completeInPredicateExpression(Expression baseExpression) {
        int from = cursor;
        boolean useNotWord = false;
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            useNotWord = true;
            from = cursor;
            word = parseStatementWord();
        }
        if (!SW_IN.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'IN'", from);
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
        int from = cursor;
        char symol = getSymbol();
        if ('(' != symol) {
            throw createException("Ожидается начало блока значений '(', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        from = cursor;
        ValueListExpression inValueExpr = new ValueListExpression();
        SelectableExpression valueExpr = parseSingleSelectExpr();
        while (true) {
            inValueExpr.addValueExpr(valueExpr);
            char symbol = parseDelim(FUNC_ARGS_DELIMS, true);
            if (')' == symbol) {
                break;
            }
            if (',' != symbol) {
                throw createException("Ожидается разделитель между значениями в блоке ',', а получен '" + symbol + "'", from);
            }
            moveCursor();
            from = cursor;
            valueExpr = parseSingleSelectExpr();
        }
        skipSpaces();
        from = cursor;
        if (getSymbol() != ')') {
            throw createException("Ожидается заврешение блока значений ')', а получен '" + getSymbol() + "'", from);
        }
        moveCursor();
        return inValueExpr;
    }

    private boolean checkIsLikePredicateExpression() {
        keep();
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            word = parseStatementWord();
        }
        boolean likePredicateExprFlag = SW_LIKE.equalsIgnoreCase(word);
        rollback();
        return likePredicateExprFlag;
    }

    private LikePredicateExpression completeLikePredicateExpression(Expression baseExpression) {
        int from = cursor;
        boolean useNotWord = false;
        String word = parseStatementWord();
        if (SW_NOT.equalsIgnoreCase(word)) {
            useNotWord = true;
            from = cursor;
            word = parseStatementWord();
        }
        if (!SW_LIKE.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'LIKE'", from);
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
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_ESCAPE.equalsIgnoreCase(word);
    }

    private Expression parseEscapeExpression() {
        int from = cursor;
        String word = parseStatementWord();
        if (!SW_ESCAPE.equalsIgnoreCase(word)) {
            throw createException("Ожидается служебное слово 'ESCAPE', а получено '" + word + "'", from);
        }
        return parseSingleSelectExpr();
    }

    private boolean checkIsBetweenPredicateExpression() {
        keep();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null || !wordInfo.isSqlWord()) {
            rollback();
            return false;
        }
        if (SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            wordInfo = parseWordInfo();
            if (wordInfo == null || !wordInfo.isSqlWord()) {
                rollback();
                return false;
            }
        }
        if (!SW_BETWEEN.equalsIgnoreCase(wordInfo.getWord())) {
            rollback();
            return false;
        }
        rollback();
        return true;
    }

    private BetweenPredicateExpression completeBetweenPredicateExpression(Expression baseExpression) {
        WordInfo wordInfo = parseWordInfo();
        int from = cursor;
        boolean useNotWord = false;
        if (wordInfo != null && SW_NOT.equalsIgnoreCase(wordInfo.getWord())) {
            useNotWord = true;
            from = cursor;
            wordInfo = parseWordInfo();
        }
        if (wordInfo == null || !SW_BETWEEN.equalsIgnoreCase(wordInfo.getWord())) {
            throw createException("Ожидается 'BETWEEN', а получено '" + (wordInfo == null ? "null" : wordInfo.getWord()) + "'", from);
        }
        moveCursor(-1);
        BetweenPredicateExpression betweenPredicateExpr = new BetweenPredicateExpression();
        betweenPredicateExpr.setBaseExpression(baseExpression);
        betweenPredicateExpr.setUseNotWord(useNotWord);
        Expression leftExpr = parseFullSingleRowValueConstructorExpr();
        betweenPredicateExpr.setLeftExpression(leftExpr);
        from = cursor;
        ConditionFlowType conditionFlowType = parseConditionFlowType();
        if (ConditionFlowType.CFT_AND != conditionFlowType) {
            throw createException("Ожидается 'AND', а получено '" + (conditionFlowType == null ? "null" : conditionFlowType.getCondition())
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
        keep();
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo == null) {
            rollback();
            return null;
        }
        if (EOF != wordInfo.getStopSymbol()) {
            moveCursor(-1);
        }
        String conditionWord = wordInfo.getWord();
        ConditionFlowType condType = resolveConditionFlowType(conditionWord);
        if (condType == null) {
            rollback();
        } else {
            popupKeep();
        }
        return condType;
    }

    private boolean checkIsValuesComparisonPredicateExpression() {
        skipSpaces();
        return ',' == getSymbol();
    }

    private ComparisonPredicateExpression completeValuesComparisonPredicateExpression(Expression firstExpr, ParenLevels levels) {
        int from = cursor;
        if (levels.isEmpty()) {
            throw createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        ValueListExpression leftValuesExpr = new ValueListExpression();
        leftValuesExpr.addValueExpr(firstExpr);
        skipSpaces();
        while (',' == getSymbol()) {
            moveCursor();
            from = cursor;
            Expression valueExpr = parseSelectableExpression();
            skipSpaces();
            leftValuesExpr.addValueExpr(valueExpr);
        }
        from = cursor;
        if (')' != getSymbol()) {
            throw createException("Ожидается заврешение <row_value_constructor> символом ')', а получен '" + getSymbol() + "'", from);
        }
        ParenLevel level = levels.pop();
        if (!level.isEmpty()) {
            throw createException("Нарушен баланс открывающихся и закрывающихся скобок", from);
        }
        levels.decDepth();
        moveCursor();
        ComparisonOperatorType operType = parseComparisonOperatorType();
        ValueListExpression rightExpr = parseValueListExpression();
        ComparisonPredicateExpression compPredicateExpr = new ComparisonPredicateExpression();
        compPredicateExpr.setLeftExpr(leftValuesExpr);
        compPredicateExpr.setCompOperatorType(operType);
        compPredicateExpr.setRightExpr(rightExpr);
        return compPredicateExpr;
    }

    private boolean checkIsGroupByStatement() {
        keep();
        String firstWord = parseStatementWord();
        String secondWord = parseStatementWord();
        rollback();
        return SW_GROUP.equalsIgnoreCase(firstWord) && SW_BY.equalsIgnoreCase(secondWord);
    }

    private GroupByStatement parseGroupByStatement() {
        if (checkIsGroupByStatement()) {
            String firstWord = parseStatementWord();
            String secondWord = parseStatementWord();
            if (!SW_GROUP.equalsIgnoreCase(firstWord) || !SW_BY.equalsIgnoreCase(secondWord)) {
                throw createException("Ожидается секция 'GROUP BY', а имеем '" + firstWord + " " + secondWord + "'", cursor);
            }
            GroupByStatement groupByStatement = new GroupByStatement();
            GroupingColumnReferenceExpression firstRefColumnExpr = parseGroupingColumnReferenceExpression();
            groupByStatement.addGroupByExpr(firstRefColumnExpr);
            skipSpaces();
            while (',' == getSymbol()) {
                moveCursor();
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
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_HAVING.equalsIgnoreCase(word);
    }

    private HavingStatement parseHavingStatement() {
        if (checkIsHavingStatement()) {
            String word = parseStatementWord();
            if (!SW_HAVING.equalsIgnoreCase(word)) {
                throw createException("Ожидается секция 'HAVING', а имеем '" + word + "'", cursor);
            }
            Expression searchExpr = parseFullSearchConditionsExpr();
            HavingStatement havingStatementExpr = new HavingStatement();
            havingStatementExpr.setSearchExpr(searchExpr);
            return havingStatementExpr;
        }
        return null;
    }

    private OrderByStatement parseOrderByStatement() {
        keep();
        String orderWord = parseStatementWord();
        if (!"ORDER".equalsIgnoreCase(orderWord)) {
            rollback();
            return null;
        }
        popupKeep();
        int from = cursor;
        String byWord = parseStatementWord();
        if (!SW_BY.equalsIgnoreCase(byWord)) {
            throw createException("Ожидается секция 'ORDER BY', а имеем 'ORDER " + byWord + "'", from);
        }
        from = cursor;
        OrderByStatement orderByStatement = new OrderByStatement();
        SortKeyExpression firstSortKeyExpr = parseSortKeyExpression();
        orderByStatement.addSortKeyExpr(firstSortKeyExpr);
        while (',' == getSymbol()) {
            moveCursor();
            SortKeyExpression nextSortKeyExpr = parseSortKeyExpression();
            orderByStatement.addSortKeyExpr(nextSortKeyExpr);
        }

        return orderByStatement;
    }

    private SortKeyExpression parseSortKeyExpression() {
        int from = cursor;
        Expression sortKeyValueExpr = null;
        if (checkIsColumnExpr()) {
            sortKeyValueExpr = parseColumnExpr();
            // TODO проверить, что это простая колонка
        } else if (checkIsNumericExpr()) {
            sortKeyValueExpr = parseNumericExpr();
            // TODO проверить, что это число без мантис точек и прочего лишнего
        }
        if (sortKeyValueExpr == null) {
            throw createException("Ожидается значение сортировки в секции 'ORDER BY'", from);
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
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_COLLATE.equalsIgnoreCase(word);
    }

    private CollateExpression parseCollateExpression() {
        int from = cursor;
        String word = parseStatementWord();
        if (!SW_COLLATE.equalsIgnoreCase(word)) {
            throw createException("Ожидается секция 'COLLATE', а имеем '" + word + "'", from);
        }
        Expression collationNameExpr = parseColumnExpr();
        CollateExpression collateExpr = new CollateExpression();
        collateExpr.setCollationNameExpr(collationNameExpr);

        return collateExpr;
    }

    private boolean checkIsOrderingSpecification() {
        keep();
        String word = parseStatementWord();
        rollback();
        return SW_ASC.equalsIgnoreCase(word) || SW_DESC.equalsIgnoreCase(word);
    }

    private String parseOrderingSpecification() {
        int from = cursor;
        String word = parseStatementWord();
        if (!(SW_ASC.equalsIgnoreCase(word) || SW_DESC.equalsIgnoreCase(word))) {
            throw createException("Ожидается служебное слово 'ASC' или 'DESC', а имеем '" + word + "'", from);
        }
        return word;
    }

    /**
     *
     * @return в верхнем регистре
     */
    private String parseStatementWord() {
        return parseStatementWord(true);
    }

    private String parseStatementWord(boolean toUpperCase) {
        WordInfo wordInfo = parseWordInfo();
        if (wordInfo != null) {
            String word = wordInfo.getWord();
            cursor = wordInfo.getAfterWordPos();
            return toUpperCase ? word.toUpperCase() : word;
        }
        return "";
    }

    private static final List<Character> DONT_SQL_WORD_LETTERS = Arrays.asList(
            '.', ':', '(', ')', '!', '?', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0');

    private WordInfo parseWordInfo() {
        skipSpaces();
        int from = cursor;
        int afterWordPos = cursor;
        while (!DONT_SQL_WORD_LETTERS.contains(getSymbol())) {
            if (Character.isWhitespace(getSymbol())) {
                int tempPos = cursor;
                skipSpaces();
                if (DONT_SQL_WORD_LETTERS.contains(getSymbol())) {
                    break;
                }
                cursor = tempPos;
                break;
            }
            moveCursor();
            afterWordPos = cursor;
        }
        String word = null;
        if (cursor != from) {
            word = query.substring(from, cursor).trim();
            char delim = getSymbol();
            moveCursor();
            return new WordInfo(word, afterWordPos, delim);
        }
        return null;
    }

    private char parseDelim(List<Character> delims, boolean throwFlag) {
        skipSpaces();
        char symbol = getSymbol();
        if (!delims.contains(symbol)) {
            if (throwFlag) {
                throw createException("Ожидается разделитель один из '" + delims + "', а получен символ '" + symbol + "'", cursor);
            }
            symbol = EOF;
        }
        return symbol;
    }

    private boolean checkIsIdentifier() {
        skipSpaces();
        char symbol = getSymbol();
        if (EOF == symbol || !Character.isJavaIdentifierPart(symbol)) {
            return false;
        }
        keep();
        WordInfo holder = parseWordInfo();
        rollback();
        return holder != null && !holder.isSqlWord();
    }

    private void skipSpaces() {
        while (Character.isWhitespace(getSymbol())) {
            moveCursor();
        }
    }

    private char getSymbol() {
        return getSymvol(0);
    }

    private void moveCursor() {
        moveCursor(1);
    }

    private void moveCursor(int offset) {
        int pos = cursor + offset;
        if (offset == 1) {
            if (getSymbol() == EOL) {
                lineNumber++;
            }
            cursor = pos;
        } else if (offset == -1) {
            if (getSymbol() == EOL) {
                lineNumber--;
            }
            cursor = pos;
        } else if (offset > 0) {
            while (cursor != pos) {
                if (getSymbol() == EOL) {
                    lineNumber++;
                }
                cursor++;
            }
        } else if (offset < 0) {
            while (cursor != pos) {
                if (getSymbol() == EOL) {
                    lineNumber--;
                }
                cursor--;
            }
        }
    }

    private char getSymvol(int offset) {
        int pos = offset + cursor;
        if (pos < 0 || pos >= query.length()) {
            return EOF;
        }
        return query.charAt(pos);
    }

    private void keep() {
        markers.push(new Marker(cursor, lineNumber));
    }

    private void popupKeep() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        markers.pop();
    }

    private void rollback() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        Marker marker = markers.pop();
        cursor = marker.getCursor();
        lineNumber = marker.getLineNumber();
    }

    private IllegalStateException createException(String message, int from) {
        return createException(message, from, null);
    }

    private IllegalStateException createException(String message, int from, Throwable cause) {
        StringBuilder errorText = new StringBuilder();
        errorText.append(message);
        errorText.append(". Позиция '").append(cursor).append("'. Номер строки '").append(lineNumber + 1);
        errorText.append("'. Выражение '").append(query).append("'");
        if (from < query.length()) {
            int end = Math.min(from + 15, query.length());
            String scanPart = query.substring(from, end) + "...";
            errorText.append(". Текущий анализ остановлен на '").append(scanPart).append("'");
        }
        return new IllegalStateException(errorText.toString(), cause);
    }
}

package com.reforms.sql.parser;

import com.reforms.sql.expr.term.SqlWords;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Поток для работы с sql выражениями
 * TODO превисти к единообразию логику проверки check и parse, а именно или все функции check должны выражаться через parse или parse всегда должен через check
 *      лучше так: check = parse != null;
 * @author evgenie
 */
public class SqlStream extends AbstractSqlStream {

    private static final char EOL = '\n';

    private static final char EOF = '\0';

    private String query;

    private int cursor;

    private int lineNumber;

    private Deque<SqlParserState> markers = new ArrayDeque<>();

    public SqlStream(String query) {
        this.query = query;
    }

    //-------------------------- OPERATOR API ------------------------- \\
    @Override
    public boolean checkIsMathOperatorValue() {
        keepParserState();
        String mathOperatorValue = parseMathOperatorValue();
        rollbackParserState();
        return mathOperatorValue != null;
    }

    private static final List<Character> MATH_OPERAND = Arrays.asList('+', '-', '*', '/', '|');

    @Override
    public String parseMathOperatorValue() {
        skipSpaces();
        char symbol = getSymbol();
        if (MATH_OPERAND.contains(symbol)) {
            if ('|' != symbol) {
                moveCursor();
                return String.valueOf(symbol);
            }
            char secondSymbol = getSymbol(1);
            if ('|' == secondSymbol) {
                moveCursor(2);
                return "||";
            }
        }
        return null;
    }

    @Override
    public boolean checkIsComparisonOperatorValue() {
        keepParserState();
        String operatorValue = parseComparisonOperatorValue();
        rollbackParserState();
        return operatorValue != null;
    }

    @Override
    public String parseComparisonOperatorValue() {
        skipSpaces();
        char firstSymbol = getSymbol();
        moveCursor();
        char secondSymbol = getSymbol();
        moveCursor();
        if ('=' == firstSymbol) {
            moveCursor(-1);
            return "=";
        }
        if ('!' == firstSymbol && '=' == secondSymbol) {
            return "!=";
        }
        if ('<' == firstSymbol) {
            if ('>' == secondSymbol) {
                return "<>";
            }
            if ('=' == secondSymbol) {
                return "<=";
            }
            moveCursor(-1);
            return "<";
        }
        if ('>' == firstSymbol) {
            if ('=' == secondSymbol) {
                return ">=";
            }
            moveCursor(-1);
            return ">";
        }
        moveCursor(-2);
        return null;
    }

    //-------------------------- TOKEN API ------------------------- \\
    @Override
    public boolean checkIsFilterValue() {
        skipSpaces();
        return ':' == getSymbol();
    }

    @Override
    public String parseFilterValue() {
        return parseIdentifierValue(DONT_SQL_FILTER_IDENTIFIER_CHARS);
    }

    @Override
    public boolean checkIsQuestionValue() {
        skipSpaces();
        return '?' == getSymbol();
    }

    @Override
    public boolean checkIsAsteriskValue() {
        skipSpaces();
        return '*' == getSymbol();
    }

    @Override
    public boolean checkIsNumericValue() {
        skipSpaces();
        char symbol = getSymbol();
        return '-' == symbol || '+' == symbol || Character.isDigit(symbol);
    }

    @Override
    public String parseNumericValue() {
        if (checkIsNumericValue()) {
            int from = getCursor();
            boolean wasDot = false;
            boolean wasE = false;
            while (true) {
                char symbol = getSymbol();
                if (('+' == symbol || '-' == symbol) && from == getCursor()) {
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
                if (('E' == symbol || 'e' == symbol) && !wasE) {
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
            if (from == getCursor()) {
                throw createException("Не является числом", from);
            }
            char prevSymbol = getSymbol(-1);
            if (getCursor() - from == 1 && ('+' == prevSymbol || '-' == prevSymbol)) {
                throw createException("Ожидается после знака '+' или '-' хотя бы 1 число!", from);
            }
            String numericValue = getValueFrom(from);
            return numericValue;
        }
        return null;
    }

    @Override
    public boolean checkIsStringValue() {
        skipSpaces();
        return '\'' == getSymbol();
    }

    @Override
    public String parseStringValue() {
        if (checkIsStringValue()) {
            char symbol = getSymbol();
            int from = getCursor();
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
            return getValueFrom(from);
        }
        return null;
    }

    @Override
    public String parseDoubleQuoteValue() {
        skipSpaces();
        int from = getCursor();
        char symbol = getSymbol();
        if ('"' != symbol) {
            return null;
        }
        moveCursor();
        while ('"' != (symbol = getSymbol()) && symbol != '\0') {
            moveCursor();
        }
        if (symbol == '\0') {
            throw createException("Не является строкой в двойных кавычках", from);
        }
        moveCursor();
        String doubleQuoteValue = getValueFrom(from);
        return doubleQuoteValue;
    }

    @Override
    public boolean checkIsSpecialWordValueOneOf(String ... checkedWords) {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        rollbackParserState();
        if (specialWordValue != null) {
            for (String checkedWord : checkedWords) {
                if (specialWordValue.equalsIgnoreCase(checkedWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String parseSpecialWordValueAndCheckOneOf(String ... checkedWords) {
        int from = getCursor();
        String specialWordValue = parseSpecialWordValueVariants(checkedWords);
        if (specialWordValue == null) {
            throw createException("Ожидается ключевое слово одно из '" + Arrays.asList(checkedWords) + "', а получено '" + specialWordValue + "'", from);
        }
        return specialWordValue;
    }

    @Override
    public String parseSpecialWordValueVariants(String ... variantWords) {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        if (specialWordValue != null) {
            for (String variantWord : variantWords) {
                if (specialWordValue.equalsIgnoreCase(variantWord)) {
                    skipParserState();
                    return specialWordValue;
                }
            }
        }
        rollbackParserState();
        return null;
    }

    @Override
    public boolean checkIsSpecialWordValueSame(String word) {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        rollbackParserState();
        return word != null && specialWordValue != null && word.equalsIgnoreCase(specialWordValue);
    }

    @Override
    public String parseSpecialWordValueAndCheck(String checkedWord) {
        int from = getCursor();
        String word = parseSpecialWordValue();
        if (checkedWord == null || word == null || !checkedWord.equalsIgnoreCase(word)) {
            throw createException("Ожидается ключевое слово '" + checkedWord + "', а получено '" + word + "'", from);
        }
        return word;
    }

    @Override
    public boolean checkIsSpecialWordValue() {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        rollbackParserState();
        return SqlWords.isSqlWord(specialWordValue);
    }

    @Override
    public String parseSpecialWordValue(boolean toUpperCase) {
        String specialWordValue = parseIdentifierValue();
        if (specialWordValue != null && toUpperCase) {
            specialWordValue = specialWordValue.toUpperCase();
        }
        return specialWordValue;
    }

    @Override
    public boolean checkIsIdentifierValue(boolean ignoreSqlSpecialWord) {
        keepParserState();
        String identifier = parseIdentifierValue();
        rollbackParserState();
        return !(identifier == null || (SqlWords.isSqlWord(identifier) && ignoreSqlSpecialWord));
    }

    /**
     * Убраны '.', ':', '#' и '?'
     * TODO: переделать в MAP
     */
    private static final List<Character> DONT_SQL_FILTER_IDENTIFIER_CHARS = Arrays.asList(
            '(', ')', '!', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0', ' ');

    /**
     * Убраны '.', ':' и '#'
     * TODO: переделать в MAP
     */
    private static final List<Character> DONT_SQL_EXT_IDENTIFIER_CHARS = Arrays.asList(
            '?', '(', ')', '!', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0', ' ');

    @Override
    public String parseMetaIdentifierValue() {
        return parseIdentifierValue(DONT_SQL_EXT_IDENTIFIER_CHARS);
    }

    /**
     * TODO: переделать в MAP
     */
    private static final List<Character> DONT_SQL_IDENTIFIER_CHARS = Arrays.asList(
            '.', ':', '#', '?', '(', ')', '!', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0', ' ');

    @Override
    public String parseIdentifierValue() {
        return parseIdentifierValue(DONT_SQL_IDENTIFIER_CHARS);
    }

    private String parseIdentifierValue(List<Character> dontSqlWordLetters) {
        skipSpaces();
        int from = getCursor();
        while (!dontSqlWordLetters.contains(getSymbol())) {
            moveCursor();
        }
        String word = null;
        if (from != getCursor()) {
            word = getValueFrom(from).trim();
        }
        return word;
    }

    @Override
    public String getValueFrom(int from) {
        return query.substring(from, cursor);
    }

    @Override
    public void skipSpaces() {
        while (Character.isWhitespace(getSymbol())) {
            moveCursor();
        }
    }

    //-------------------------- SYMBOL API ------------------------- \\
    @Override
    public char getSymbol(int offset) {
        int pos = offset + cursor;
        if (pos < 0 || pos >= query.length()) {
            return EOF;
        }
        return query.charAt(pos);
    }

    @Override
    public int getCursor() {
        return cursor;
    };

    //TODO удалить - решение временно
    @Override
    public void changeCursor(int newPosCursor) {
        cursor = newPosCursor;
    }

    @Override
    public void moveCursor(int offset) {
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

    @Override
    public boolean finished() {
        return cursor >= query.length();
    }

    //-------------------------- STATE API ------------------------- \\
    @Override
    public void keepParserState() {
        markers.push(new SqlParserState(cursor, lineNumber));
    }

    @Override
    public void skipParserState() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        markers.pop();
    }

    @Override
    public void rollbackParserState() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        SqlParserState marker = markers.pop();
        cursor = marker.getCursor();
        lineNumber = marker.getLineNumber();
    }

    //------------------------ EXCEPTION API ----------------------- \\
    @Override
    public IllegalStateException createException(String message, int from, Throwable cause) {
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

    @Override
    public String toString() {
        return query;
    }
}

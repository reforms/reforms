package com.reforms.sql.parser;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import com.reforms.sql.expr.term.SqlWords;

/**
 * Поток для работы с sql выражениями
 * @author evgenie
 */
public class SqlStream implements ISqlStream {

    private static final char EOL = '\n';

    private static final char EOF = '\0';

    private String query;

    private int cursor;

    private int lineNumber;

    private Deque<SqlParserState> markers = new ArrayDeque<>();

    public SqlStream(String query) {
        this.query = query;
    }

    //-------------------------- TOKEN API ------------------------- \\
    @Override
    public String parseDoubleQuoteValue() {
        skipSpaces();
        int from = getCursor();
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
        String doubleQuoteValue = getValueFrom(from);
        return doubleQuoteValue;
    }

    private static final List<Character> DONT_SQL_IDENTIFIER_CHARS = Arrays.asList(
            '.', ':', '(', ')', '!', '?', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0', ' ');

    @Override
    public boolean checkIsIdentifierValue() {
        keepParserState();
        String identifier = parseIdentifierValue();
        rollbackParserState();
        return identifier != null && !SqlWords.isSqlWord(identifier);
    }

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
    public char getSymbol() {
        return getSymvol(0);
    }

    @Override
    public char getSymvol(int offset) {
        int pos = offset + cursor;
        if (pos < 0 || pos >= query.length()) {
            return EOF;
        }
        return query.charAt(pos);
    }

    public int getCursor() {
        return cursor;
    };

    //TODO удалить - решение временно
    @Override
    public void changeCursor(int newPosCursor) {
        this.cursor = newPosCursor;
    }

    @Override
    public void moveCursor() {
        moveCursor(1);
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
    public IllegalStateException createException(String message) {
        return createException(message, null);
    }

    @Override
    public IllegalStateException createException(String message, Throwable cause) {
        return createException(message, cursor, cause);
    }

    @Override
    public IllegalStateException createException(String message, int from) {
        return createException(message, from, null);
    }

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
}

package com.reforms.sql.parser;

import java.util.*;

/**
 * Поток для работы с sql выражениями
 * TODO превисти к единообразию логику проверки check и parse, а именно или все функции check должны выражаться через parse или parse всегда должен через check
 *      лучше так: check = parse != null;
 * @author evgenie
 */
public class SqlStream {

    private static final char TAB_SYMBOL = '\t';

    private static final char R_SYMBOL = '\r';

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
    /**
     * Проверить, является ли значение математическим операторами
     * @return true - значение является математическим оператором
     */
    public boolean checkIsMathOperatorValue() {
        keepParserState();
        String mathOperatorValue = parseMathOperatorValue();
        rollbackParserState();
        return mathOperatorValue != null;
    }

    private static final List<Character> MATH_OPERAND = Arrays.asList('+', '-', '*', '/', '|');

    /**
     * Распарсить значение математического оператора
     * @return значение математического оператора
     *         или NULL, если это не оператор сравнения
     */
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

    /**
     * Распарсить значение оператора сравнения
     * @return true - значение является оператором сравнения
     */
    public boolean checkIsComparisonOperatorValue() {
        keepParserState();
        String operatorValue = parseComparisonOperatorValue();
        rollbackParserState();
        return operatorValue != null;
    }

    /**
     * Распарсить значение оператора сравнения
     * @return значение оператора сравнения
     *         или NULL, если это не оператор сравнения
     */
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
    /**
     * Проверить, является ли значение фильтром для вставки значения
     * @return true - значение является  фильтром для вставки значения
     */
    public boolean checkIsFilterValue() {
        skipSpaces();
        return ':' == getSymbol();
    }

    /**
     * Распарсить фильтр для вставки значения
     * @return фильтр для вставки значения
     *         или NULL, если это не числовое значение
     */
    public String parseFilterValue() {
        return parseIdentifierValue(DONT_SQL_FILTER_IDENTIFIER_CHARS);
    }

    /**
     * Проверить, является ли значение '?'
     * @return true - значение '?', false иначе
     */
    public boolean checkIsQuestionValue() {
        skipSpaces();
        return '?' == getSymbol();
    }

    /**
     * Распарсить '?'
     * @return символ '?'
     * @throws IllegalStateException не является '?'
     */
    public char parseQuestionValueAndCheck() {
        if (!checkIsQuestionValue()) {
            throw createException("Ожидается '?', а получен символ " + getCharName(getSymbol()));
        }
        moveCursor();
        return '?';
    }

    /**
     * Проверить, является ли значение '*'
     * @return true - значение '*', false иначе
     */
    public boolean checkIsAsteriskValue() {
        skipSpaces();
        return '*' == getSymbol();
    }

    /**
     * Распарсить '*'
     * @return символ '*'
     * @throws IllegalStateException не является '*'
     */
    public char parseAsteriskValueAndCheck() {
        if (!checkIsAsteriskValue()) {
            throw createException("Ожидается символ '*', а получен символ " + getCharName(getSymbol()));
        }
        moveCursor();
        return '*';
    }

    /**
     * Проверить, является ли следующий токен числовым значением или нет
     * @return true следующий токен является числовым значением
     */
    public boolean checkIsNumericValue() {
        skipSpaces();
        char symbol = getSymbol();
        return '-' == symbol || '+' == symbol || Character.isDigit(symbol);
    }

    /**
     * Распарсить числовое значение
     * @return числовое значение
     *         или NULL, если это не числовое значение
     */
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

    /**
     * Проверить, является ли следующий токен строковым значением или нет
     * @return true следующий токен является строковым значением
     */
    public boolean checkIsStringValue() {
        skipSpaces();
        return '\'' == getSymbol();
    }

    /**
     * Распарсить строковое значение
     * @return строковое значение
     *         или NULL, если это не строковое значение
     */
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

    /**
     * Распарсить значение в двойных кавычках
     * @return значение в двойных кавычках
     *         или NULL, если это не значение в двойных кавычках
     */
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

    /**
     * Проверить, что указанная последовательность служебных слов выполняется
     * @param sequentWords последовательность служебных слов
     * @return true - указанная последовательность служебных слов выполняется
     */
    public boolean checkIsSpecialWordSequents(OptWord ... sequentWords) {
        keepParserState();
        String result = parseSpecialWordSequents(sequentWords);
        rollbackParserState();
        return result != null;
    }

    /**
     * Распарсить последовательность служебных слов
     * @param sequentWords последовательность служебных слов
     * @return последовательность служебных слов
     *         или NULL, если это не последовательность служебных слов
     */
    public String parseSpecialWordSequents(OptWord ... sequentWords) {
        keepParserState();
        StringBuilder result = null;
        for (OptWord sequentWord : sequentWords) {
            if (checkIsSpecialWordValueSame(sequentWord.getWord())) {
                String word = parseSpecialWordValue();
                if (result != null) {
                    result.append(" ");
                } else {
                    result = new StringBuilder();
                }
                result.append(word);
            } else if (sequentWord.isRequired()) {
                rollbackParserState();
                return null;
            }
        }
        skipParserState();
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    /**
     * Проверить, что следующий токен одного из указанного значения
     * @param words возможные варианты
     * @return true - следующий токен одного из указанного значения
     */
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

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param checkedWords значения для проверки
     * @return ключевое слово как есть без изменения регистра букв
     * @throws IllegalStateException указанное слово не совпадает ни с одним из заданных
     */
    public String parseSpecialWordValueAndCheckOneOf(String ... checkedWords) {
        int from = getCursor();
        String specialWordValue = parseSpecialWordValueVariants(checkedWords);
        if (specialWordValue == null) {
            throw createException("Ожидается ключевое слово одно из '" + Arrays.asList(checkedWords) + "', а получено '" + specialWordValue + "'", from);
        }
        return specialWordValue;
    }

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param variantWords варианты значений
     * @return ключевое слово как есть без изменения регистра букв
     *         или NULL, если это не логически-значимая часть sql выражения
     */
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

    /**
     * Проверить, что следующий токен указанного значения
     * @param word токен указанного значения
     * @return true - следующий токен указанного значения
     */
    public boolean checkIsSpecialWordValueSame(String word) {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        rollbackParserState();
        return word != null && specialWordValue != null && word.equalsIgnoreCase(specialWordValue);
    }

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param checkedWord значение для проверки
     * @return ключевое слово как есть без изменения регистра букв
     * @throws IllegalStateException указанное слово не совпадает с заданным
     */
    public String parseSpecialWordValueAndCheck(String checkedWord) {
        int from = getCursor();
        String word = parseSpecialWordValue();
        if (checkedWord == null || word == null || !checkedWord.equalsIgnoreCase(word)) {
            throw createException("Ожидается ключевое слово '" + checkedWord + "', а получено '" + word + "'", from);
        }
        return word;
    }

    /**
     * Проверить является ли значение ключевым словом SQL
     * @return true - ключевое слово SQL, false - иначе
     */
    public boolean checkIsSpecialWordValue() {
        keepParserState();
        String specialWordValue = parseSpecialWordValue();
        rollbackParserState();
        return SqlWords.isSqlWord(specialWordValue);
    }

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово
     * @param toUpperCase признак того, что результат нужно приводить к верхнему регистру
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public String parseSpecialWordValue() {
        return parseSpecialWordValue(true);
    }

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово
     * @param toUpperCase признак того, что результат нужно приводить к верхнему регистру
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public String parseSpecialWordValue(boolean toUpperCase) {
        String specialWordValue = parseIdentifierValue();
        if (specialWordValue != null && toUpperCase) {
            specialWordValue = specialWordValue.toUpperCase();
        }
        return specialWordValue;
    }

    /**
     * Проверить, является ли это логически-значимой частью sql выражения
     * @param ignoreSqlSpecialWord признак того, что ключевые слова игнорируются
     * @return true - это логически-значимой частью sql выражения
     */
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

    /**
     * Распарсить логически-значимую часть sql выражения и мета информацию в рамках этого идентификатора (доп символы: '#', '.', ':')
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public String parseMetaIdentifierValue() {
        return parseIdentifierValue(DONT_SQL_EXT_IDENTIFIER_CHARS);
    }

    /**
     * TODO: переделать в MAP
     */
    private static final List<Character> DONT_SQL_IDENTIFIER_CHARS = Arrays.asList(
            '.', ':', '#', '?', '(', ')', '!', '<', '>', '=', ',', '*', '+', '-', '/', '&', '^', '%', '~', '"', '\'', '\0', ' ');

    /**
     * Распарсить логически-значимую часть sql выражения
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
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

    /**
     * Получить часть query от позиции from до позиции текущего куросра
     * @param from позиция от
     * @return часть query от позиции from до позиции текущего куросра
     */
    public String getValueFrom(int from) {
        return query.substring(from, cursor);
    }

    //-------------------------- DELIM AND WHITESPACES API ------------------------- \\
    /**
     * Пропустить все текущие пробелы
     */
    public void skipSpaces() {
        while (Character.isWhitespace(getSymbol())) {
            moveCursor();
        }
    }

    public static List<Character> FUNC_DELIMS = Arrays.asList(',', ')');

    /**
     * Проверить текущий символ на открывающуюся скобку
     * @throws IllegalStateException другой символ
     */
    public void checkIsOpenParent() {
        checkIsOpenParent(true);
    }

    /**
     * Проверить текущий символ на открывающуюся скобку
     * @param throwMode если true кинется ошибка
     * @throws IllegalStateException другой символ
     */
    public boolean checkIsOpenParent(boolean throwMode) {
        return checkIsDelim('(', throwMode);
    }

    /**
     * Проверить текущий символ на закрывающуюся скобку
     * @throws IllegalStateException другой символ
     */
    public boolean checkIsCloseParen() {
        return checkIsCloseParen(true);
    }

    /**
     * Проверить текущий символ на закрывающуюся скобку
     * @param throwMode если true кинется ошибка
     * @throws IllegalStateException другой символ
     */
    public boolean checkIsCloseParen(boolean throwMode) {
        return checkIsDelim(')', throwMode);
    }

    /**
     * Проверить что разделить: '(', ','.
     * @return true разделить один из '(', ','.
     * @throws IllegalStateException разделитель указанного типа не найден
     */
    public boolean checkIsFuncArgsDelim() {
        skipSpaces();
        char symbol = getSymbol();
        return FUNC_DELIMS.contains(symbol);
    }

    /**
     * Распарсить разделить: '(', ','.
     * @return один из указанных разделителей
     * @throws IllegalStateException разделитель указанного типа не найден
     */
    public char parseFuncArgsDelim() {
        return parseDelim(FUNC_DELIMS);
    }

    /**
     * Распарсить разделить.
     * @param oneOfDelims допустимые разделители
     * @return один из указанных разделителей
     * @throws IllegalStateException разделитель указанного типа не найден
     */
    public char parseDelim(List<Character> oneOfDelims) {
        skipSpaces();
        char symbol = getSymbol();
        if (oneOfDelims.contains(symbol)) {
            return symbol;
        }
        List<String> chars = new ArrayList<>();
        for (char expectedDelim : oneOfDelims) {
            String charName = getCharName(expectedDelim);
            chars.add(charName);
        }
        String wasCharName = getCharName(symbol);
        throw createException("Ожидается разделитель один из [" + chars + "], а получен символ [" + wasCharName + "]");
    }

    /**
     * Проверить текущий символ
     * @param delim     проверяемый символ
     * @param throwMode если true кинется ошибка
     * @throws IllegalStateException другой символ
     */
    public boolean checkIsDelim(char delim, boolean throwMode) {
        skipSpaces();
        char symbol = getSymbol();
        if (delim != symbol) {
            if (throwMode) {
                String delimName = getCharName(delim);
                String charName = getCharName(symbol);
                throw createException("Ожидается символ " + delimName + ", а получен " + charName + "'");
            }
            return false;
        }
        return true;
    }

    private String getCharName(char symbol) {
        if (EOF == symbol) {
            return "'конец файла'";
        }
        if (EOL == symbol) {
            return "'конец строки'";
        }
        if (R_SYMBOL == symbol) {
            return "'перевод каретки'";
        }
        if (TAB_SYMBOL == symbol) {
            return "'табуляция'";
        }
        return "'" + symbol + "'";
    }

    //-------------------------- SYMBOL API ------------------------- \\
    /**
     * Получить текущий символ
     * @return текущий символ
     */
    public char getSymbol() {
        return getSymbol(0);
    }

    /**
     * Получить символ со смещением offset
     * @param offset смещение
     * @return символ со смещением offset
     */
    public char getSymbol(int offset) {
        int pos = offset + cursor;
        if (pos < 0 || pos >= query.length()) {
            return EOF;
        }
        return query.charAt(pos);
    }

    /**
     * Получить значение текущего куросора
     * @return значение текущего куросора
     */
    public int getCursor() {
        return cursor;
    };

    /**
     * Передвинуть курсор на единицу вперед
     */
    public void moveCursor() {
        moveCursor(1);
    }

    /**
     * Передвинуть курсор на смещение offset
     * @param offset смещение
     */
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

    /**
     * Проверить, что курсор указывает на конец query
     * @return true - курсор указывает на конец query, false - иначе
     */
    public boolean finished() {
        return cursor >= query.length();
    }

    //-------------------------- STATE API ------------------------- \\
    /**
     * Сохранить текущие параметры потока в стек
     */
    public void keepParserState() {
        markers.push(new SqlParserState(cursor, lineNumber));
    }

    /**
     * Выталкнуть из стека параметры потока
     */
    public void skipParserState() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        markers.pop();
    }

    /**
     * Выталкнуть из стека параметры потока и применить их к текущему состоянию
     */
    public void rollbackParserState() {
        if (markers.isEmpty()) {
            throw new IllegalStateException("Не возможно откатитить состояние парсера");
        }
        SqlParserState marker = markers.pop();
        cursor = marker.getCursor();
        lineNumber = marker.getLineNumber();
    }

    //------------------------ EXCEPTION API ----------------------- \\
    public IllegalStateException createException(String message) {
        return createException(message, null);
    }

    public IllegalStateException createException(String message, Throwable cause) {
        return createException(message, getCursor(), cause);
    }

    public IllegalStateException createException(String message, int from) {
        return createException(message, from, null);
    }

    /**
     * Сформировать сообщение об ошибке
     * @param message сообщение об ошибке
     * @param from    позиция для отображения доп информации
     * @param cause   причина или NULL
     * @return сообщение об ошибке
     */
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
package com.reforms.sql.parser;




/**
 *
 * @author evgenie
 */
public abstract class AbstractSqlStream {

    //-------------------------- OPERATOR API ------------------------- \\
    /**
     * Проверить, является ли значение математическим операторами
     * @return true - значение является математическим оператором
     */
    public abstract boolean checkIsMathOperatorValue();

    /**
     * Распарсить значение математического оператора
     * @return значение математического оператора
     *         или NULL, если это не оператор сравнения
     */
    public abstract String parseMathOperatorValue();

    /**
     * Распарсить значение оператора сравнения
     * @return true - значение является оператором сравнения
     */
    public abstract boolean checkIsComparisonOperatorValue();

    /**
     * Распарсить значение оператора сравнения
     * @return значение оператора сравнения
     *         или NULL, если это не оператор сравнения
     */
    public abstract String parseComparisonOperatorValue();

    //-------------------------- TOKEN API ------------------------- \\
    /**
     * Проверить, является ли значение фильтром для вставки значения
     * @return true - значение является  фильтром для вставки значения
     */
    public abstract boolean checkIsFilterValue();

    /**
     * Распарсить фильтр для вставки значения
     * @return фильтр для вставки значения
     *         или NULL, если это не числовое значение
     */
    public abstract String parseFilterValue();

    /**
     * Проверить, является ли значение '?'
     * @return true - значение '?', false иначе
     */
    public abstract boolean checkIsQuestionValue();

    /**
     * Проверить, является ли значение '*'
     * @return true - значение '*', false иначе
     */
    public abstract boolean checkIsAsteriskValue();

    /**
     * Проверить, является ли следующий токен числовым значением или нет
     * @return true следующий токен является числовым значением
     */
    public abstract boolean checkIsNumericValue();

    /**
     * Распарсить числовое значение
     * @return числовое значение
     *         или NULL, если это не числовое значение
     */
    public abstract String parseNumericValue();

    /**
     * Проверить, является ли следующий токен строковым значением или нет
     * @return true следующий токен является строковым значением
     */
    public abstract boolean checkIsStringValue();

    /**
     * Распарсить строковое значение
     * @return строковое значение
     *         или NULL, если это не строковое значение
     */
    public abstract String parseStringValue();

    /**
     * Распарсить значение в двойных кавычках
     * @return значение в двойных кавычках
     *         или NULL, если это не значение в двойных кавычках
     */
    public abstract String parseDoubleQuoteValue();

    /**
     * Проверить, что следующий токен одного из указанного значения
     * @param words возможные варианты
     * @return true - следующий токен одного из указанного значения
     */
    public abstract boolean checkIsSpecialWordValueOneOf(String ... checkedWords);

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param checkedWords значения для проверки
     * @return ключевое слово как есть без изменения регистра букв
     * @throws IllegalStateException указанное слово не совпадает ни с одним из заданных
     */
    public abstract String parseSpecialWordValueAndCheckOneOf(String ... checkedWords);

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param variantWords варианты значений
     * @return ключевое слово как есть без изменения регистра букв
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public abstract String parseSpecialWordValueVariants(String ... variantWords);

    /**
     * Проверить, что следующий токен указанного значения
     * @param word токен указанного значения
     * @return true - следующий токен указанного значения
     */
    public abstract boolean checkIsSpecialWordValueSame(String word);

    /**
     * Распарсить логически-значимую часть sql выражения - ключевое слово и проверить, что совпадает с указанным значением
     * @param checkedWord значение для проверки
     * @return ключевое слово как есть без изменения регистра букв
     * @throws IllegalStateException указанное слово не совпадает с заданным
     */
    public abstract String parseSpecialWordValueAndCheck(String checkedWord);

    /**
     * Проверить является ли значение ключевым словом SQL
     * @return true - ключевое слово SQL, false - иначе
     */
    public abstract boolean checkIsSpecialWordValue();

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
    public abstract String parseSpecialWordValue(boolean toUpperCase);

    /**
     * Проверить, является ли это логически-значимой частью sql выражения
     * @param ignoreSqlSpecialWord признак того, что ключевые слова игнорируются
     * @return true - это логически-значимой частью sql выражения
     */
    public abstract boolean checkIsIdentifierValue(boolean ignoreSqlSpecialWord);

    /**
     * Распарсить логически-значимую часть sql выражения и мета информацию в рамках этого идентификатора (доп символы: '#', '.', ':')
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public abstract String parseMetaIdentifierValue();

    /**
     * Распарсить логически-значимую часть sql выражения
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    public abstract String parseIdentifierValue();

    /**
     * Получить часть query от позиции from до позиции текущего куросра
     * @param from позиция от
     * @return часть query от позиции from до позиции текущего куросра
     */
    public abstract String getValueFrom(int from);

    /**
     * Пропустить все текущие пробелы
     */
    public abstract void skipSpaces();

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
    public abstract char getSymbol(int offset);

    /**
     * Получить значение текущего куросора
     * @return значение текущего куросора
     */
    public abstract int getCursor();

    //TODO удалить - решение временно
    public abstract void changeCursor(int newPosCursor);

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
    public abstract void moveCursor(int offset);

    /**
     * Проверить, что курсор указывает на конец query
     * @return true - курсор указывает на конец query, false - иначе
     */
    public abstract boolean finished();

    //-------------------------- STATE API ------------------------- \\
    /**
     * Сохранить текущие параметры потока в стек
     */
    public abstract void keepParserState();

    /**
     * Выталкнуть из стека параметры потока
     */
    public abstract void skipParserState();

    /**
     * Выталкнуть из стека параметры потока и применить их к текущему состоянию
     */
    public abstract void rollbackParserState();

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

    public abstract IllegalStateException createException(String message, int from, Throwable cause);
}

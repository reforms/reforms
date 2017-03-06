package com.reforms.sql.parser;

public interface ISqlStream {

    //-------------------------- TOKEN API ------------------------- \\
    /**
     * Распарсить значение в двойных кавычках
     * @return значение в двойных кавычках
     */
    String parseDoubleQuoteValue();

    boolean checkIsIdentifierValue();

    /**
     * Получить логически-значимую часть sql выражения
     * @return логически-значимая часть sql выражения
     *         или NULL, если это не логически-значимая часть sql выражения
     */
    String parseIdentifierValue();

    /**
     * Получить часть query от позиции from до позиции текущего куросра
     * @param from позиция от
     * @return часть query от позиции from до позиции текущего куросра
     */
    String getValueFrom(int from);

    /**
     * Пропустить все текущие пробелы
     */
    void skipSpaces();

    //-------------------------- SYMBOL API ------------------------- \\
    /**
     * Получить текущий символ
     * @return текущий символ
     */
    char getSymbol();

    /**
     * Получить символ со смещением offset
     * @param offset смещение
     * @return символ со смещением offset
     */
    char getSymvol(int offset);

    /**
     * Получить значение текущего куросора
     * @return значение текущего куросора
     */
    int getCursor();

    //TODO удалить - решение временно
    void changeCursor(int newPosCursor);

    /**
     * Передвинуть курсор на единицу вперед
     */
    void moveCursor();

    /**
     * Передвинуть курсор на смещение offset
     * @param offset смещение
     */
    void moveCursor(int offset);

    /**
     * Проверить, что курсор указывает на конец query
     * @return true - курсор указывает на конец query, false - иначе
     */
    boolean finished();

    //-------------------------- STATE API ------------------------- \\
    /**
     * Сохранить текущие параметры потока в стек
     */
    void keepParserState();

    /**
     * Выталкнуть из стека параметры потока
     */
    void skipParserState();

    /**
     * Выталкнуть из стека параметры потока и применить их к текущему состоянию
     */
    void rollbackParserState();

    //------------------------ EXCEPTION API ----------------------- \\
    IllegalStateException createException(String message);

    IllegalStateException createException(String message, Throwable cause);

    IllegalStateException createException(String message, int from);

    IllegalStateException createException(String message, int from, Throwable cause);
}

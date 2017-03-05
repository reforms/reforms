package com.reforms.sql.parser;

public interface ISqlStream {

    //-------------------------- TOKEN API ------------------------- \\
    String getPartFrom(int from);

    //-------------------------- SYMBOL API ------------------------- \\
    char getSymbol();

    char getSymvol(int offset);

    int getCursor();

    //TODO удалить - решение временно
    void changeCursor(int newPosCursor);

    void moveCursor();

    void moveCursor(int offset);

    boolean finished();

    //-------------------------- STATE API ------------------------- \\
    void keepParserState();

    void skipParserState();

    void rollbackParserState();

    //------------------------ EXCEPTION API ----------------------- \\
    IllegalStateException createException(String message);

    IllegalStateException createException(String message, Throwable cause);

    IllegalStateException createException(String message, int from);

    IllegalStateException createException(String message, int from, Throwable cause);
}

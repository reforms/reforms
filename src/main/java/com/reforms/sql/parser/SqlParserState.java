package com.reforms.sql.parser;

class SqlParserState {

    private int cursor;

    private int lineNumber;

    SqlParserState(int cursor, int lineNumber) {
        this.cursor = cursor;
        this.lineNumber = lineNumber;
    }

    int getCursor() {
        return cursor;
    }

    int getLineNumber() {
        return lineNumber;
    }
}
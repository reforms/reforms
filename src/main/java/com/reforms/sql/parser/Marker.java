package com.reforms.sql.parser;

class Marker {

    private int cursor;

    private int lineNumber;

    Marker(int cursor, int lineNumber) {
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
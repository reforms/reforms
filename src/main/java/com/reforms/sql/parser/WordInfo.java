package com.reforms.sql.parser;

import com.reforms.sql.expr.term.SqlWords;

/**
 * TODO: нужно переосмыслить и удалить
 * @author evgenie
 */
class WordInfo {

    private String word;

    private int afterWordPos;

    private char stopSymbol;

    WordInfo(String word, int afterWordPos, char stopSymbol) {
        this.word = word;
        this.afterWordPos = afterWordPos;
        this.stopSymbol = stopSymbol;
    }

    String getWord() {
        return word;
    }

    int getAfterWordPos() {
        return afterWordPos;
    }

    char getStopSymbol() {
        return stopSymbol;
    }

    boolean isSqlWord() {
        return SqlWords.isSqlWord(word);
    }

    @Override
    public String toString() {
        return "word=" + word + "\nstop='" + stopSymbol + "'\nafterWord = " + afterWordPos;
    }
}
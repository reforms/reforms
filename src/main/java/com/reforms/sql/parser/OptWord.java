package com.reforms.sql.parser;

/**
 *
 * @author evgenie
 */
public class OptWord {

    private final boolean required;

    private final String word;

    public OptWord(boolean required, String word) {
        this.required = required;
        this.word = word;
    }

    public boolean isRequired() {
        return required;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "[required=" + required + ", word=" + word + "]";
    }
}

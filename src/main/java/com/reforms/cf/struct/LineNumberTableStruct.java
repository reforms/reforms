package com.reforms.cf.struct;

/**
 * The LineNumberTable struct
 *
 * @author evgenie
 *
 */
public class LineNumberTableStruct {
    private int startPc; // u2
    private int lineNumber; // u2

    public int getStartPc() {
        return startPc;
    }

    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LineNumberTable startPc=").append(startPc).append(", lineNumber=").append(lineNumber);
        return builder.toString();
    }

}

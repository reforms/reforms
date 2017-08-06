package com.reforms.cf.struct;

/**
 * Each table indicates a range of code array offsets within which a local
 * variable has a value
 *
 * @author evgenie
 *
 */
public class LocalvarTableStruct {
    private int startPc; // u2
    private int length; // u2
    private int index; // u2

    public int getStartPc() {
        return startPc;
    }

    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LocalvarTable startPc=").append(startPc).append(", length=").append(length).append(", index=")
                .append(index);
        return builder.toString();
    }

}

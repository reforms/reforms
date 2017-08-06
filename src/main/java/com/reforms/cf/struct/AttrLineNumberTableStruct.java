package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The LineNumberTable Attribute
 *
 * @author evgenie
 */
public class AttrLineNumberTableStruct extends AttrBaseStruct {

    private int lineNumberTableLength; // u2
    private LineNumberTableStruct[] lineNumberTable;

    public AttrLineNumberTableStruct() {
        super(Attributes.LINE_NUMBER_TABLE_ATTR);
    }

    public int getLineNumberTableLength() {
        return lineNumberTableLength;
    }

    public void setLineNumberTableLength(int lineNumberTableLength) {
        this.lineNumberTableLength = lineNumberTableLength;
    }

    public LineNumberTableStruct[] getLineNumberTable() {
        return lineNumberTable;
    }

    public void setLineNumberTable(LineNumberTableStruct[] lineNumberTable) {
        this.lineNumberTable = lineNumberTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" lineNumberTableLength=")
                .append(lineNumberTableLength).append(", lineNumberTable=").append(Arrays.toString(lineNumberTable));
        return builder.toString();
    }

}

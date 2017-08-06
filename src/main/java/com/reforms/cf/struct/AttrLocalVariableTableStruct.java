package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The LocalVariableTable Attribute
 *
 * @author evgenie
 */
public class AttrLocalVariableTableStruct extends AttrBaseStruct {

    private int localVariableTableLength; // u2
    private LocalVariableTableStruct[] localVariableTable;

    public AttrLocalVariableTableStruct() {
        super(Attributes.LOCAL_VARIABLE_TABLE_ATTR);
    }

    public int getLocalVariableTableLength() {
        return localVariableTableLength;
    }

    public void setLocalVariableTableLength(int localVariableTableLength) {
        this.localVariableTableLength = localVariableTableLength;
    }

    public LocalVariableTableStruct[] getLocalVariableTable() {
        return localVariableTable;
    }

    public void setLocalVariableTable(LocalVariableTableStruct[] localVariableTable) {
        this.localVariableTable = localVariableTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" localVariableTableLength=")
                .append(localVariableTableLength).append(", localVariableTable=")
                .append(Arrays.toString(localVariableTable));
        return builder.toString();
    }

}

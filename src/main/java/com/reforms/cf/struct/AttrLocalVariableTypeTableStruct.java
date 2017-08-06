package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The LocalVariableTypeTable Attribute
 *
 * @author evgenie
 */
public class AttrLocalVariableTypeTableStruct extends AttrBaseStruct {

    private int localVariableTypeTableLength; // u2
    private LocalVariableTypeTableStruct[] localVariableTypeTable;

    public AttrLocalVariableTypeTableStruct() {
        super(Attributes.LOCAL_VARIABLE_TYPE_TABLE_ATTR);
    }

    public int getLocalVariableTypeTableLength() {
        return localVariableTypeTableLength;
    }

    public void setLocalVariableTypeTableLength(int localVariableTypeTableLength) {
        this.localVariableTypeTableLength = localVariableTypeTableLength;
    }

    public LocalVariableTypeTableStruct[] getLocalVariableTypeTable() {
        return localVariableTypeTable;
    }

    public void setLocalVariableTypeTable(LocalVariableTypeTableStruct[] localVariableTypeTable) {
        this.localVariableTypeTable = localVariableTypeTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" localVariableTypeTableLength=")
                .append(localVariableTypeTableLength).append(", localVariableTypeTable=")
                .append(Arrays.toString(localVariableTypeTable));
        return builder.toString();
    }

}

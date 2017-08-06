package com.reforms.cf.struct;

/**
 * The Parameter struct
 *
 * @author evgenie
 */
public class ParameterStruct {
    private int nameIndex; // u2
    private AccessFlags accessFlags; // u2

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public AccessFlags getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(AccessFlags accessFlags) {
        this.accessFlags = accessFlags;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Parameter nameIndex=").append(nameIndex).append(", accessFlags=").append(accessFlags);
        return builder.toString();
    }

}

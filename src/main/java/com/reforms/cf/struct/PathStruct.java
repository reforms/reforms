package com.reforms.cf.struct;

/**
 * The Path struct
 *
 * @author evgenie
 */
public class PathStruct {
    private int typePathKind; // u1
    private int typeArgumentIndex; // u1

    public int getTypePathKind() {
        return typePathKind;
    }

    public void setTypePathKind(int typePathKind) {
        this.typePathKind = typePathKind;
    }

    public int getTypeArgumentIndex() {
        return typeArgumentIndex;
    }

    public void setTypeArgumentIndex(int typeArgumentIndex) {
        this.typeArgumentIndex = typeArgumentIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Path typePathKind=").append(typePathKind).append(", typeArgumentIndex=")
                .append(typeArgumentIndex);
        return builder.toString();
    }

}

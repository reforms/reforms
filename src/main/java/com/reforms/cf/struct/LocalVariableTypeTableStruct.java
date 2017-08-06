package com.reforms.cf.struct;

/**
 * The LocalVariableTypeTable struct
 *
 * @author evgenie
 *
 */
public class LocalVariableTypeTableStruct {

    private int startPc; // u2
    private int length; // u2
    private int nameIndex; // u2
    private int signatureIndex; // u2
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

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public int getSignatureIndex() {
        return signatureIndex;
    }

    public void setSignatureIndex(int signatureIndex) {
        this.signatureIndex = signatureIndex;
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
        builder.append("LocalVariableTypeTable startPc=").append(startPc).append(", length=").append(length)
                .append(", nameIndex=").append(nameIndex).append(", signatureIndex=").append(signatureIndex)
                .append(", index=").append(index);
        return builder.toString();
    }

}

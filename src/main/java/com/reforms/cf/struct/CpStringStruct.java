package com.reforms.cf.struct;

/**
 * The CONSTANT_String_info Structure
 *
 * @author evgenie
 */
public class CpStringStruct extends CpItemStruct {

    private int stringIndex; // u2

    public CpStringStruct() {
        super(ConstantPool.STRING_TAG);
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_STRING_TAG = ").append(getTag()).append(", stringIndex=").append(stringIndex);
        return builder.toString();
    }



}

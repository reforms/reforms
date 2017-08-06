package com.reforms.cf.struct;

/**
 * The CONSTANT_Class_info Structure
 * @author evgenie
 */
public class CpClassStruct extends CpItemStruct {

    private int nameIndex; // u2

    public CpClassStruct() {
        super(ConstantPool.CLASS_TAG);
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_CLASS_TAG = ").append(getTag()).append(", nameIndex=").append(nameIndex);
        return builder.toString();
    }

}

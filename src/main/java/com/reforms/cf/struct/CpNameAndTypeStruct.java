package com.reforms.cf.struct;

/**
 * The CONSTANT_NameAndType_info Structure
 *
 * @author evgenie
 */
public class CpNameAndTypeStruct extends CpItemStruct {

    private int nameIndex; // u2
    private int descriptorIndex; // u2

    public CpNameAndTypeStruct() {
        super(ConstantPool.NAME_AND_TYPE_TAG);
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_NAME_AND_TYPE_TAG = ").append(getTag()).append(", nameIndex=").append(nameIndex)
                .append(", descriptorIndex=").append(descriptorIndex);
        return builder.toString();
    }

}

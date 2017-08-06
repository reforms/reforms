package com.reforms.cf.struct;

/**
 * The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
 * CONSTANT_InterfaceMethodref_info Structures
 *
 * @author evgenie
 */
public class CpMemberRefStruct extends CpItemStruct {

    private int classIndex; // u2
    private int nameAndTypeIndex; // u2

    public CpMemberRefStruct(int tag) {
        super(tag);
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }

    public void setNameAndTypeIndex(int nameAndTypeIndex) {
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_").append(ConstantPool.getTagName(getTag())).append(" = ").append(getTag())
                .append(", classIndex=").append(classIndex).append(", nameAndTypeIndex=").append(nameAndTypeIndex);
        return builder.toString();
    }
}

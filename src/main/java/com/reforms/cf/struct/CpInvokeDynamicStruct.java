package com.reforms.cf.struct;

/**
 * The CONSTANT_InvokeDynamic_info Structure
 *
 * @author evgenie
 */
public class CpInvokeDynamicStruct extends CpItemStruct {

    private int bootstrapMethodAttrIndex; // u2
    private int nameAndTypeIndex; // u2

    public CpInvokeDynamicStruct() {
        super(ConstantPool.INVOKE_DYNAMIC_TAG);
    }

    public int getBootstrapMethodAttrIndex() {
        return bootstrapMethodAttrIndex;
    }

    public void setBootstrapMethodAttrIndex(int bootstrapMethodAttrIndex) {
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
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
        builder.append("CP_INVOKE_DYNAMIC_TAG = ").append(getTag()).append(", bootstrapMethodAttrIndex=")
                .append(bootstrapMethodAttrIndex).append(", nameAndTypeIndex=").append(nameAndTypeIndex);
        return builder.toString();
    }

}

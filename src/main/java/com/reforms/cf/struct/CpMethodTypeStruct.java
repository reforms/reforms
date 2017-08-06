package com.reforms.cf.struct;

/**
 * The CONSTANT_MethodType_info Structure
 * @author evgenie
 */
public class CpMethodTypeStruct extends CpItemStruct {

    private int descriptorIndex; // u2

    public CpMethodTypeStruct() {
        super(ConstantPool.METHOD_TYPE_TAG);
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
        builder.append("CP_METHOD_TYPE_TAG = ").append(getTag()).append(", descriptorIndex=").append(descriptorIndex);
        return builder.toString();
    }

}

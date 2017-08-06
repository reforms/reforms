package com.reforms.cf.struct;

/**
 * The ConstantValue Attribute
 *
 * @author evgenie
 */
public class AttrConstantValueStruct extends AttrBaseStruct {

    private int constantValueIndex;

    public AttrConstantValueStruct() {
        super(Attributes.CONSTANT_VALUE_ATTR);
    }

    public int getConstantValueIndex() {
        return constantValueIndex;
    }

    public void setConstantValueIndex(int constantValueIndex) {
        this.constantValueIndex = constantValueIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(", constantValueIndex=").append(constantValueIndex);
        return builder.toString();
    }



}

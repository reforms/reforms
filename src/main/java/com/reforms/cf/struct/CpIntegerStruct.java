package com.reforms.cf.struct;

/**
 * The CONSTANT_Integer_info
 *
 * @author evgenie
 */
public class CpIntegerStruct extends CpItemStruct {

    private int value; // u4;

    public CpIntegerStruct() {
        super(ConstantPool.INTEGER_TAG);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_INTEGER_TAG = ").append(getTag()).append(", value=").append(value);
        return builder.toString();
    }

}

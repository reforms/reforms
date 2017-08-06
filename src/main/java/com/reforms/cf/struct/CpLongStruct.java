package com.reforms.cf.struct;

/**
 * The CONSTANT_Long_info
 *
 * @author evgenie
 */
public class CpLongStruct extends CpItemStruct {

    private long value; // u8;

    public CpLongStruct() {
        super(ConstantPool.LONG_TAG);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_LONG_TAG = ").append(getTag()).append(", value=").append(value);
        return builder.toString();
    }

}

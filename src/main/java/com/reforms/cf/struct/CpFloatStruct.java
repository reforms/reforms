package com.reforms.cf.struct;

/**
 * The CONSTANT_Float_info
 *
 * @author evgenie
 */
public class CpFloatStruct extends CpItemStruct {

    private float value; // u4;

    public CpFloatStruct() {
        super(ConstantPool.FLOAT_TAG);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_FLOAT_TAG = ").append(getTag()).append(", value=").append(value);
        return builder.toString();
    }

}

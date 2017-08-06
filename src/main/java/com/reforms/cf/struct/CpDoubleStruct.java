package com.reforms.cf.struct;

/**
 * The CONSTANT_Double_info
 *
 * @author evgenie
 */
public class CpDoubleStruct extends CpItemStruct {

    private double value; // u8;

    public CpDoubleStruct() {
        super(ConstantPool.DOUBLE_TAG);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_DOUBLE_TAG = ").append(getTag()).append(", value=").append(value);
        return builder.toString();
    }

}

package com.reforms.cf.struct;


/**
 * The CONSTANT_NameAndType_info Structure
 *
 * @author evgenie
 */
public class CpUtf8Struct extends CpItemStruct {

    private int length; // u2
    private String value; // u1Array

    public CpUtf8Struct() {
        super(ConstantPool.UTF8_TAG);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_UTF8_TAG = ").append(getTag()).append(", length=").append(length).append(", value=")
                .append(value);
        return builder.toString();
    }

}

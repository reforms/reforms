package com.reforms.cf.struct;

/**
 * Attribute - Unknown, for 1.9/2.0 and more java version
 *
 * @author evgenie
 */
public class AttrUnknownStruct extends AttrBaseStruct {

    private byte[] data;

    public AttrUnknownStruct() {
        super(Attributes.UNKNOWN_ATTR);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" data_length=")
                .append(data == null ? 0 : data.length);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

/**
 * The element value byte struct
 *
 * @author evgenie
 *
 */
public class ElementValueByteStruct extends ElementValueStruct {

    private byte value;

    public ElementValueByteStruct() {
        super(ElementValues.BYTE_NAME_EV, ElementValues.BYTE_EV);
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=")
                .append(value);
        return builder.toString();
    }

}

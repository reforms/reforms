package com.reforms.cf.struct;

/**
 * The element value short struct
 *
 * @author evgenie
 *
 */
public class ElementValueShortStruct extends ElementValueStruct {

    private short value;

    public ElementValueShortStruct() {
        super(ElementValues.SHORT_NAME_EV, ElementValues.SHORT_EV);
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
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

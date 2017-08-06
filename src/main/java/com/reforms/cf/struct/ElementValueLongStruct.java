package com.reforms.cf.struct;

/**
 * The element value long struct
 *
 * @author evgenie
 *
 */
public class ElementValueLongStruct extends ElementValueStruct {

    private long value;

    public ElementValueLongStruct() {
        super(ElementValues.LONG_NAME_EV, ElementValues.LONG_EV);
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
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=")
                .append(value);
        return builder.toString();
    }

}

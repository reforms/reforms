package com.reforms.cf.struct;

/**
 * The element value int struct
 *
 * @author evgenie
 *
 */
public class ElementValueIntStruct extends ElementValueStruct {

    private int value;

    public ElementValueIntStruct() {
        super(ElementValues.INT_NAME_EV, ElementValues.INT_EV);
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
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=")
                .append(value);
        return builder.toString();
    }

}

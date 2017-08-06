package com.reforms.cf.struct;

/**
 * The element value double struct
 *
 * @author evgenie
 *
 */
public class ElementValueDoubleStruct extends ElementValueStruct {

    private double value;

    public ElementValueDoubleStruct() {
        super(ElementValues.DOUBLE_NAME_EV, ElementValues.DOUBLE_EV);
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
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=")
                .append(value);
        return builder.toString();
    }

}

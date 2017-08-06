package com.reforms.cf.struct;

/**
 * The element value float struct
 *
 * @author evgenie
 *
 */
public class ElementValueFloatStruct extends ElementValueStruct {

    private float value;

    public ElementValueFloatStruct() {
        super(ElementValues.FLOAT_NAME_EV, ElementValues.FLOAT_EV);
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
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=")
                .append(value);
        return builder.toString();
    }

}

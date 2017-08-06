package com.reforms.cf.struct;

/**
 * The element value boolean struct
 *
 * @author evgenie
 *
 */
public class ElementValueBooleanStruct extends ElementValueStruct {

    private boolean value;

    public ElementValueBooleanStruct() {
        super(ElementValues.BOOLEAN_NAME_EV, ElementValues.BOOLEAN_EV);
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
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

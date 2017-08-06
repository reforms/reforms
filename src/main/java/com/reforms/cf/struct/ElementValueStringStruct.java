package com.reforms.cf.struct;

/**
 * The element value String struct
 *
 * @author evgenie
 *
 */
public class ElementValueStringStruct extends ElementValueStruct {

    private String value;

    public ElementValueStringStruct() {
        super(ElementValues.STRING_NAME_EV, ElementValues.STRING_EV);
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
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' value=\"")
                .append(value).append("\"");
        return builder.toString();
    }

}

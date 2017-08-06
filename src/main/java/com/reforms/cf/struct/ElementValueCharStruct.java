package com.reforms.cf.struct;

/**
 * The element value int struct
 *
 * @author evgenie
 *
 */
public class ElementValueCharStruct extends ElementValueStruct {

    private char value;

    public ElementValueCharStruct() {
        super(ElementValues.CHAR_NAME_EV, ElementValues.CHAR_EV);
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
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

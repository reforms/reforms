package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The element value Array struct
 *
 * @author evgenie
 */
public class ElementValueArrayStruct extends ElementValueStruct {

    private int numValues; // u2
    private ElementValueStruct[] values;

    public ElementValueArrayStruct() {
        super(ElementValues.ARRAY_NAME_EV, ElementValues.ARRAY_EV);
    }

    public int getNumValues() {
        return numValues;
    }

    public void setNumValues(int numValues) {
        this.numValues = numValues;
    }

    public ElementValueStruct[] getValues() {
        return values;
    }

    public void setValues(ElementValueStruct[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' numValues=")
                .append(numValues).append(", values=").append(Arrays.toString(values));
        return builder.toString();
    }

}

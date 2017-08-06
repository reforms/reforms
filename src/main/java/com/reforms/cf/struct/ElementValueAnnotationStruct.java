package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The element value Annotation struct
 *
 * @author evgenie
 */
public class ElementValueAnnotationStruct extends ElementValueStruct {

    private int typeIndex; // u2
    private int numElementValuePairs; // u2
    private ElementValuePairStruct[] elementValuePairs;

    public ElementValueAnnotationStruct() {
        super(ElementValues.ANNOTATION_NAME_EV, ElementValues.ANNOTATION_EV);
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public int getNumElementValuePairs() {
        return numElementValuePairs;
    }

    public void setNumElementValuePairs(int numElementValuePairs) {
        this.numElementValuePairs = numElementValuePairs;
    }

    public ElementValuePairStruct[] getElementValuePairs() {
        return elementValuePairs;
    }

    public void setElementValuePairs(ElementValuePairStruct[] elementValuePairs) {
        this.elementValuePairs = elementValuePairs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' typeIndex=")
                .append(typeIndex).append(", numElementValuePairs=").append(numElementValuePairs)
                .append(", elementValuePairs=").append(Arrays.toString(elementValuePairs));
        return builder.toString();
    }

}

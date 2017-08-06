package com.reforms.cf.struct;

/**
 * The ElementValuePair Struct
 *
 * @author evgenie
 */
public class ElementValuePairStruct {
    private int elementNameIndex; // u2
    private ElementValueStruct elementValue;

    public int getElementNameIndex() {
        return elementNameIndex;
    }

    public void setElementNameIndex(int elementNameIndex) {
        this.elementNameIndex = elementNameIndex;
    }

    public ElementValueStruct getElementValue() {
        return elementValue;
    }

    public void setElementValue(ElementValueStruct elementValue) {
        this.elementValue = elementValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValuePairStruct [elementNameIndex=").append(elementNameIndex).append(", elementValue=")
                .append(elementValue).append("]");
        return builder.toString();
    }

}

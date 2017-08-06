package com.reforms.cf.struct;

/**
 * The AnnotationDefault Attribute
 *
 * @author evgenie
 */
public class AttrAnnotationDefaultStruct extends AttrBaseStruct {

    private ElementValueStruct elementValue;

    public AttrAnnotationDefaultStruct() {
        super(Attributes.ANNOTATION_DEFAULT_ATTR);
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
        builder.append("Attribute: name=").append(getName()).append(" elementValue=").append(elementValue);
        return builder.toString();
    }

}

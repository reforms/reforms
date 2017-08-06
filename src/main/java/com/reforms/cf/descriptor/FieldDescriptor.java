package com.reforms.cf.descriptor;

/**
 * A field descriptor, same is FieldType, is same ReturnDescriptor
 * @author evgenie
 */
public abstract class FieldDescriptor extends Descriptor {

    private final char term;
    private final String value;

    protected FieldDescriptor(char term, String value) {
        this.term = term;
        this.value = value;
    }

    public char getTerm() {
        return term;
    }

    public String getValue() {
        return value;
    }

}

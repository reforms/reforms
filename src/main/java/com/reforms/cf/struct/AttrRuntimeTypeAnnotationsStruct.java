package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The AttrRuntimeTypeAnnotations Attribute
 *
 * @author evgenie
 */
public class AttrRuntimeTypeAnnotationsStruct extends AttrBaseStruct {

    private int numAnnotations; // u2
    private TypeAnnotationStruct[] annotations;

    protected AttrRuntimeTypeAnnotationsStruct(String name) {
        super(name);
    }

    public int getNumAnnotations() {
        return numAnnotations;
    }

    public void setNumAnnotations(int numAnnotations) {
        this.numAnnotations = numAnnotations;
    }

    public TypeAnnotationStruct[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(TypeAnnotationStruct[] annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numAnnotations=").append(numAnnotations)
                .append(", annotations=").append(Arrays.toString(annotations));
        return builder.toString();
    }

}

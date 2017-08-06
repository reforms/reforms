package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The RuntimeVisibleAnnotations Attribute
 *
 * @author evgenie
 */
public class AttrRuntimeAnnotationsStruct extends AttrBaseStruct {

    private int numAnnotations; // u2
    private ElementValueAnnotationStruct[] annotations; //

    protected AttrRuntimeAnnotationsStruct(String name) {
        super(name);
    }

    public int getNumAnnotations() {
        return numAnnotations;
    }

    public void setNumAnnotations(int numAnnotations) {
        this.numAnnotations = numAnnotations;
    }

    public ElementValueAnnotationStruct[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(ElementValueAnnotationStruct[] annotations) {
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

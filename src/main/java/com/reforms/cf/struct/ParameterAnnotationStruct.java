package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The ParameterAnnotation Struct
 *
 * @author evgenie
 */
public class ParameterAnnotationStruct {

    private int numAnnotations; // u2
    private ElementValueAnnotationStruct[] annotations;

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
        builder.append("ParameterAnnotation numAnnotations=").append(numAnnotations).append(", annotations=")
                .append(Arrays.toString(annotations));
        return builder.toString();
    }

}

package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The AttrRuntimeParameterAnnotations Attribute
 *
 * @author evgenie
 */
public class AttrRuntimeParameterAnnotationsStruct extends AttrBaseStruct {

    private int numParameters; // u1
    private ParameterAnnotationStruct[] parameterAnnotations;

    protected AttrRuntimeParameterAnnotationsStruct(String name) {
        super(name);
    }

    public int getNumParameters() {
        return numParameters;
    }

    public void setNumParameters(int numParameters) {
        this.numParameters = numParameters;
    }

    public ParameterAnnotationStruct[] getParameterAnnotations() {
        return parameterAnnotations;
    }

    public void setParameterAnnotations(ParameterAnnotationStruct[] parameterAnnotations) {
        this.parameterAnnotations = parameterAnnotations;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numParameters=").append(numParameters)
                .append(", parameterAnnotations=").append(Arrays.toString(parameterAnnotations));
        return builder.toString();
    }

}

package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The MethodParameters Attribute
 *
 * @author evgenie
 */
public class AttrMethodParametersStruct extends AttrBaseStruct {

    private int parametersCount; // u1
    private ParameterStruct[] parameters;

    public AttrMethodParametersStruct() {
        super(Attributes.METHOD_PARAMETERS_ATTR);
    }

    public int getParametersCount() {
        return parametersCount;
    }

    public void setParametersCount(int parametersCount) {
        this.parametersCount = parametersCount;
    }

    public ParameterStruct[] getParameters() {
        return parameters;
    }

    public void setParameters(ParameterStruct[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" parametersCount=").append(parametersCount)
                .append(", parameters=").append(Arrays.toString(parameters));
        return builder.toString();
    }

}

package com.reforms.cf.descriptor;



/**
 * The MethodDescriptor
 * @author evgenie
 */
public class MethodDescriptor extends Descriptor {

    private final FieldDescriptor[] parameters;
    private final FieldDescriptor returnType;

    public MethodDescriptor(FieldDescriptor[] parameters, FieldDescriptor returnType) {
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public FieldDescriptor[] getParameters() {
        return parameters;
    }

    public FieldDescriptor getParameter(int index) {
        return index >= 0 && index < getParametersCount() ? parameters[index] : null;
    }

    public int getParametersCount() {
        return parameters != null ? parameters.length : 0;
    }

    public boolean hasParameters() {
        return getParametersCount() > 0;
    }

    public FieldDescriptor getReturnType() {
        return returnType;
    }

    @Override
    public String getDescriptor() {
        StringBuilder report = new StringBuilder();
        report.append("(");
        if (hasParameters()) {
            for (int index = 0; index < parameters.length; index++) {
                report.append(parameters[index].getDescriptor());
            }
        }
        report.append(")").append(returnType.getDescriptor());
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MethodDescriptor: ").append(getDescriptor());
        return builder.toString();
    }



}

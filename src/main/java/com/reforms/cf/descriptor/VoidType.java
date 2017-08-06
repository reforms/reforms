package com.reforms.cf.descriptor;

/**
 * The VoidType: V
 * @author evgenie
 *
 */
public class VoidType extends BaseType {

    public VoidType() {
        super(Descriptors.VOID_TYPE);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VoidType '").append(getTerm()).append("' is ").append(getValue());
        return builder.toString();
    }

}

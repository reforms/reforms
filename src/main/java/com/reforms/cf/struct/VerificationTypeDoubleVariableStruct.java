package com.reforms.cf.struct;

/**
 * The Double_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeDoubleVariableStruct extends VerificationTypeStruct {

    public VerificationTypeDoubleVariableStruct() {
        super(VerificationTypes.DOUBLE_VARIABLE_NAME, VerificationTypes.DOUBLE_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

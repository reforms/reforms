package com.reforms.cf.struct;

/**
 * The Float_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeFloatVariableStruct extends VerificationTypeStruct {

    public VerificationTypeFloatVariableStruct() {
        super(VerificationTypes.FLOAT_VARIABLE_NAME, VerificationTypes.FLOAT_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

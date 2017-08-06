package com.reforms.cf.struct;

/**
 * The Integer_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeIntegerVariableStruct extends VerificationTypeStruct {

    public VerificationTypeIntegerVariableStruct() {
        super(VerificationTypes.INTEGER_VARIABLE_NAME, VerificationTypes.INTEGER_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

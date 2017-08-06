package com.reforms.cf.struct;

/**
 * The Null_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeNullVariableStruct extends VerificationTypeStruct {

    public VerificationTypeNullVariableStruct() {
        super(VerificationTypes.NULL_VARIABLE_NAME, VerificationTypes.NULL_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

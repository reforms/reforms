package com.reforms.cf.struct;

/**
 * The Top_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeTopVariableStruct extends VerificationTypeStruct {

    public VerificationTypeTopVariableStruct() {
        super(VerificationTypes.TOP_VARIABLE_NAME, VerificationTypes.TOP_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

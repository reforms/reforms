package com.reforms.cf.struct;

/**
 * The Long_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeLongVariableStruct extends VerificationTypeStruct {

    public VerificationTypeLongVariableStruct() {
        super(VerificationTypes.LONG_VARIABLE_NAME, VerificationTypes.LONG_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

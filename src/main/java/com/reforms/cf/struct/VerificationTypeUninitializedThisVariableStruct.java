package com.reforms.cf.struct;

/**
 * The UninitializedThis_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeUninitializedThisVariableStruct extends VerificationTypeStruct {

    public VerificationTypeUninitializedThisVariableStruct() {
        super(VerificationTypes.UNINITIALIZED_THIS_VARIABLE_NAME, VerificationTypes.UNINITIALIZED_THIS_VARIABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

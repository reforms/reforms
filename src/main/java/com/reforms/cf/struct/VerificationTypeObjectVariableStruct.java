package com.reforms.cf.struct;

/**
 * The Object_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeObjectVariableStruct extends VerificationTypeStruct {

    private int cpoolIndex; // u2;

    public VerificationTypeObjectVariableStruct() {
        super(VerificationTypes.OBJECT_VARIABLE_NAME, VerificationTypes.OBJECT_VARIABLE_TAG);
    }

    public int getCpoolIndex() {
        return cpoolIndex;
    }

    public void setCpoolIndex(int cpoolIndex) {
        this.cpoolIndex = cpoolIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" cpoolIndex=").append(cpoolIndex);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

/**
 * The Uninitialized_variable_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeUninitializedVariableStruct extends VerificationTypeStruct {

    private int offset; // u2;

    public VerificationTypeUninitializedVariableStruct() {
        super(VerificationTypes.UNINITIALIZED_VARIABLE_NAME, VerificationTypes.UNINITIALIZED_VARIABLE_TAG);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" offset=").append(offset);
        return builder.toString();
    }

}

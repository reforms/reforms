package com.reforms.cf.struct;

/**
 * The append_frame struct
 *
 * @author evgenie
 */
public class StackMapFrameAppendStruct extends StackMapFrameStruct {

    private int offsetDelta; // u2
    private VerificationTypeStruct[] locals;

    public StackMapFrameAppendStruct(int tag) {
        super(StackMapFrames.APPEND_FRAME, tag);
    }

    public int getOffsetDelta() {
        return offsetDelta;
    }

    public void setOffsetDelta(int offsetDelta) {
        this.offsetDelta = offsetDelta;
    }

    public VerificationTypeStruct[] getLocals() {
        return locals;
    }

    public void setLocals(VerificationTypeStruct[] locals) {
        this.locals = locals;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" offsetDelta=").append(offsetDelta)
                .append(", locals=").append(locals);
        return builder.toString();
    }

}

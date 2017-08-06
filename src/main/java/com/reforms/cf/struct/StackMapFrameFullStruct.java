package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The append_frame struct
 *
 * @author evgenie
 */
public class StackMapFrameFullStruct extends StackMapFrameStruct {

    private int offsetDelta; // u2
    private int numberOfLocals; // u2
    private VerificationTypeStruct[] locals;
    private int numberOfStackItems; // u2
    private VerificationTypeStruct[] stack;

    public StackMapFrameFullStruct() {
        super(StackMapFrames.FULL_FRAME, StackMapFrames.FULL_FRAME_TAG);
    }

    public int getOffsetDelta() {
        return offsetDelta;
    }

    public void setOffsetDelta(int offsetDelta) {
        this.offsetDelta = offsetDelta;
    }

    public int getNumberOfLocals() {
        return numberOfLocals;
    }

    public void setNumberOfLocals(int numberOfLocals) {
        this.numberOfLocals = numberOfLocals;
    }

    public VerificationTypeStruct[] getLocals() {
        return locals;
    }

    public void setLocals(VerificationTypeStruct[] locals) {
        this.locals = locals;
    }

    public int getNumberOfStackItems() {
        return numberOfStackItems;
    }

    public void setNumberOfStackItems(int numberOfStackItems) {
        this.numberOfStackItems = numberOfStackItems;
    }

    public VerificationTypeStruct[] getStack() {
        return stack;
    }

    public void setStack(VerificationTypeStruct[] stack) {
        this.stack = stack;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" offsetDelta=").append(offsetDelta)
                .append(", numberOfLocals=").append(numberOfLocals).append(", locals=").append(Arrays.toString(locals))
                .append(", numberOfStackItems=").append(numberOfStackItems).append(", stack=")
                .append(Arrays.toString(stack));
        return builder.toString();
    }

}

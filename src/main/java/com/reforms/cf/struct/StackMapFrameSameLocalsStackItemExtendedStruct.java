package com.reforms.cf.struct;

/**
 * The same_locals_1_stack_item_frame_extended struct
 *
 * @author evgenie
 */
public class StackMapFrameSameLocalsStackItemExtendedStruct extends StackMapFrameStruct {

    private int offsetDelta; // u2
    private VerificationTypeStruct stack;

    public StackMapFrameSameLocalsStackItemExtendedStruct() {
        super(StackMapFrames.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED,
                StackMapFrames.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED_TAG);
    }

    public int getOffsetDelta() {
        return offsetDelta;
    }

    public void setOffsetDelta(int offsetDelta) {
        this.offsetDelta = offsetDelta;
    }

    public VerificationTypeStruct getStack() {
        return stack;
    }

    public void setStack(VerificationTypeStruct stack) {
        this.stack = stack;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" offsetDelta=").append(offsetDelta)
                .append(", stack=").append(stack);
        return builder.toString();
    }

}

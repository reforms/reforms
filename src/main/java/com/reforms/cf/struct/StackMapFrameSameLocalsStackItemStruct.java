package com.reforms.cf.struct;

/**
 * The same_locals_1_stack_item_frame struct
 *
 * @author evgenie
 */
public class StackMapFrameSameLocalsStackItemStruct extends StackMapFrameStruct {

    private VerificationTypeStruct stack;

    public StackMapFrameSameLocalsStackItemStruct(int tag) {
        super(StackMapFrames.SAME_LOCALS_1_STACK_ITEM_FRAME, tag);
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
        builder.append(getName()).append("-").append(getTag()).append(" stack=").append(stack);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

/**
 * The same_frame struct
 *
 * @author evgenie
 */
public class StackMapFrameSameStruct extends StackMapFrameStruct {

    public StackMapFrameSameStruct(int tag) {
        super(StackMapFrames.SAME_FRAME, tag);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag());
        return builder.toString();
    }

}

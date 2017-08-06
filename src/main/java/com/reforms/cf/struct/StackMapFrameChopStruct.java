package com.reforms.cf.struct;

/**
 * The chop_frame struct
 *
 * @author evgenie
 */
public class StackMapFrameChopStruct extends StackMapFrameStruct {

    private int offsetDelta; // u2

    public StackMapFrameChopStruct(int tag) {
        super(StackMapFrames.CHOP_FRAME, tag);
    }

    public int getOffsetDelta() {
        return offsetDelta;
    }

    public void setOffsetDelta(int offsetDelta) {
        this.offsetDelta = offsetDelta;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("-").append(getTag()).append(" offsetDelta=").append(offsetDelta);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

/**
 * The same_frame_extended struct
 *
 * @author evgenie
 */
public class StackMapFrameSameExtendedStruct extends StackMapFrameStruct {

    private int offsetDelta; // u2

    public StackMapFrameSameExtendedStruct() {
        super(StackMapFrames.SAME_FRAME_EXTENDED, StackMapFrames.SAME_FRAME_EXTENDED_TAG);
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

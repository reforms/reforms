package com.reforms.cf.struct;

/**
 * The offset_target
 *
 * @author evgenie
 */
public class TargetInfoOffsetStruct extends TargetInfoStruct {

    private int offset; // u2

    public TargetInfoOffsetStruct(int targetType) {
        super(TargetInfos.OFFSET_TARGET_NAME, targetType);
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
        builder.append("TargetInfo name=").append(getName()).append(" offset=").append(offset);
        return builder.toString();
    }

}

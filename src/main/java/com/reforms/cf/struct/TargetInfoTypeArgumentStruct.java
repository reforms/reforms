package com.reforms.cf.struct;

/**
 * The type_argument_target
 *
 * @author evgenie
 */
public class TargetInfoTypeArgumentStruct extends TargetInfoStruct {

    private int offset; // u2
    private int typeArgumentIndex; // u1

    public TargetInfoTypeArgumentStruct(int targetType) {
        super(TargetInfos.TYPE_ARGUMENT_TARGET_NAME, targetType);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTypeArgumentIndex() {
        return typeArgumentIndex;
    }

    public void setTypeArgumentIndex(int typeArgumentIndex) {
        this.typeArgumentIndex = typeArgumentIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" offset=").append(offset)
                .append(", typeArgumentIndex=").append(typeArgumentIndex);
        return builder.toString();
    }

}

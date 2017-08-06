package com.reforms.cf.struct;

/**
 * The empty_target
 *
 * @author evgenie
 */
public class TargetInfoEmptyStruct extends TargetInfoStruct {

    public TargetInfoEmptyStruct(int targetType) {
        super(TargetInfos.EMPTY_TARGET_NAME, targetType);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName());
        return builder.toString();
    }

}

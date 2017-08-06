package com.reforms.cf.struct;

/**
 * The catch_target
 *
 * @author evgenie
 */
public class TargetInfoCatchStruct extends TargetInfoStruct {

    private int exceptionTableIndex; // u2

    public TargetInfoCatchStruct() {
        super(TargetInfos.CATCH_TARGET_NAME, TargetInfos.CATCH_TARGET);
    }

    public int getExceptionTableIndex() {
        return exceptionTableIndex;
    }

    public void setExceptionTableIndex(int exceptionTableIndex) {
        this.exceptionTableIndex = exceptionTableIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" exceptionTableIndex=").append(exceptionTableIndex);
        return builder.toString();
    }

}

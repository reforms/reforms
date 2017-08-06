package com.reforms.cf.struct;

/**
 * The throws_target
 *
 * @author evgenie
 */
public class TargetInfoThrowsStruct extends TargetInfoStruct {

    private int throwsTypeIndex; // u1

    public TargetInfoThrowsStruct() {
        super(TargetInfos.THROWS_TARGET_NAME, TargetInfos.THROWS_TARGET);
    }

    public int getThrowsTypeIndex() {
        return throwsTypeIndex;
    }

    public void setThrowsTypeIndex(int throwsTypeIndex) {
        this.throwsTypeIndex = throwsTypeIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" throwsTypeIndex=").append(throwsTypeIndex);
        return builder.toString();
    }

}

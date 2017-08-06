package com.reforms.cf.struct;

/**
 * The supertype_target
 *
 * @author evgenie
 */
public class TargetInfoSupertypeStruct extends TargetInfoStruct {

    private int supertypeIndex; // u2

    public TargetInfoSupertypeStruct() {
        super(TargetInfos.SUPERTYPE_TARGET_NAME, TargetInfos.SUPERTYPE_TARGET);
    }

    public int getSupertypeIndex() {
        return supertypeIndex;
    }

    public void setSupertypeIndex(int supertypeIndex) {
        this.supertypeIndex = supertypeIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" supertypeIndex=").append(supertypeIndex);
        return builder.toString();
    }

}

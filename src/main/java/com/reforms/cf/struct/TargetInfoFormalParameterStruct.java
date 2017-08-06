package com.reforms.cf.struct;

/**
 * The formal_parameter_target
 *
 * @author evgenie
 */
public class TargetInfoFormalParameterStruct extends TargetInfoStruct {

    private int formalParameterIndex; // u1

    public TargetInfoFormalParameterStruct() {
        super(TargetInfos.FORMAL_PARAMETER_TARGET_NAME, TargetInfos.FORMAL_PARAMETER_TARGET_MC);
    }

    public int getFormalParameterIndex() {
        return formalParameterIndex;
    }

    public void setFormalParameterIndex(int formalParameterIndex) {
        this.formalParameterIndex = formalParameterIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" formalParameterIndex=").append(formalParameterIndex);
        return builder.toString();
    }

}

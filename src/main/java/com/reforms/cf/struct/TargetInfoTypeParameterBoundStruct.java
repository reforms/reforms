package com.reforms.cf.struct;

/**
 * The type_parameter_bound_target
 *
 * @author evgenie
 */
public class TargetInfoTypeParameterBoundStruct extends TargetInfoStruct {

    private int typeParameterIndex; // u1
    private int boundIndex; // u1

    public TargetInfoTypeParameterBoundStruct(int targetType) {
        super(TargetInfos.TYPE_PARAMETER_BOUND_TARGET_NAME, targetType);
    }

    public int getTypeParameterIndex() {
        return typeParameterIndex;
    }

    public void setTypeParameterIndex(int typeParameterIndex) {
        this.typeParameterIndex = typeParameterIndex;
    }

    public int getBoundIndex() {
        return boundIndex;
    }

    public void setBoundIndex(int boundIndex) {
        this.boundIndex = boundIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" typeParameterIndex=").append(typeParameterIndex)
                .append(", boundIndex=").append(boundIndex);
        return builder.toString();
    }

}

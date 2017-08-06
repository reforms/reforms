package com.reforms.cf.struct;

/**
 * The type_parameter_target
 *
 * @author evgenie
 */
public class TargetInfoTypeParameterStruct extends TargetInfoStruct {

    private int typeParameterIndex; // u1

    public TargetInfoTypeParameterStruct(int targetType) {
        super(TargetInfos.TYPE_PARAMETER_TARGET_NAME, targetType);
    }

    public int getTypeParameterIndex() {
        return typeParameterIndex;
    }

    public void setTypeParameterIndex(int typeParameterIndex) {
        this.typeParameterIndex = typeParameterIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" typeParameterIndex=").append(typeParameterIndex);
        return builder.toString();
    }

}

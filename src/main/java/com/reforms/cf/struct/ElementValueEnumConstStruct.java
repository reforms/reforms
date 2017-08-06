package com.reforms.cf.struct;

/**
 * The element value EnumConst struct
 *
 * @author evgenie
 *
 */
public class ElementValueEnumConstStruct extends ElementValueStruct {

    private int typeNameIndex; // u2
    private int constNameIndex; // u2

    public ElementValueEnumConstStruct() {
        super(ElementValues.ENUM_NAME_EV, ElementValues.ENUM_EV);
    }

    public int getTypeNameIndex() {
        return typeNameIndex;
    }

    public void setTypeNameIndex(int typeNameIndex) {
        this.typeNameIndex = typeNameIndex;
    }

    public int getConstNameIndex() {
        return constNameIndex;
    }

    public void setConstNameIndex(int constNameIndex) {
        this.constNameIndex = constNameIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag())
                .append(" typeNameIndex=").append(typeNameIndex).append(", constNameIndex=").append(constNameIndex);
        return builder.toString();
    }



}

package com.reforms.cf.struct;

/**
 * The EnclosingMethod Attribute
 *
 * @author evgenie
 */
public class AttrEnclosingMethodStruct extends AttrBaseStruct {

    private int classIndex; // u2
    private int methodIndex; // u2

    public AttrEnclosingMethodStruct() {
        super(Attributes.ENCLOSING_METHOD_ATTR);
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public int getMethodIndex() {
        return methodIndex;
    }

    public void setMethodIndex(int methodIndex) {
        this.methodIndex = methodIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(", classIndex=").append(classIndex)
                .append(", methodIndex=").append(methodIndex);
        return builder.toString();
    }

}

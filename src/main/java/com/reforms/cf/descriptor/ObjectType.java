package com.reforms.cf.descriptor;


/**
 * The ObjectType: LClassName;
 * @author evgenie
 */
public class ObjectType extends FieldDescriptor {

    private final String simpleClassName;

    public ObjectType(String classNameBinnary, String simpleClassName) {
        super(Descriptors.CLASS_NAME_TYPE, classNameBinnary);
        this.simpleClassName = simpleClassName;
    }

    @Override
    public String getDescriptor() {
        return "" + getTerm() + getValue() + ";";
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ObjectType: ").append(getDescriptor());
        return builder.toString();
    }

}

package com.reforms.cf.descriptor;

/**
 * The Descriptor
 * @author evgenie
 *
 */
public abstract class Descriptor {

    public abstract String getDescriptor();

    public boolean isBaseType() {
        return getClass() == BaseType.class;
    }

    public boolean isVoidType() {
        return getClass() == VoidType.class;
    }

    public boolean isObjectType() {
        return getClass() == ObjectType.class;
    }

    public boolean isArrayType() {
        return getClass() == ArrayType.class;
    }

    public boolean isMethodType() {
        return getClass() == MethodDescriptor.class;
    }

}

package com.reforms.cf.struct;

/**
 * The StructOwner.
 * @author evgenie
 */
public enum StructType {
    CLASS_BASE, // top level in jls
    CLASS_NESTED_INNER,
    CLASS_NESTED_STATIC,
    CLASS_NESTED_LOCAL,
    CLASS_NESTED_ANONYMOUS,

    METHOD_BASE,
    METHOD_STATIC_BLOCK, // <clinit>
    METHOD_BLOCK,
    METHOD_CONSTRUCTOR,  // <init>

    FIELD_BASE,
    FIELD_ENUM,

    ATTRIBUTE,
    PARAM;

    public boolean isClass() {
        return this == CLASS_BASE
                || this == CLASS_NESTED_INNER
                || this == CLASS_NESTED_STATIC
                || this == CLASS_NESTED_LOCAL
                || this == CLASS_NESTED_ANONYMOUS;
    }

    public boolean isBaseClass() {
        return this == CLASS_BASE;
    }

    public boolean isNestedClass() {
        return isNestedClass(true);
    }

    public boolean isNestedClass(boolean andNestedStatic) {
        return this == CLASS_NESTED_INNER
                || (andNestedStatic && this == CLASS_NESTED_STATIC)
                || this == CLASS_NESTED_LOCAL
                || this == CLASS_NESTED_ANONYMOUS;
    }

    public boolean isNestedMemberClass() {
        return this == CLASS_NESTED_INNER
                || this == CLASS_NESTED_STATIC;
    }

    public boolean isNestedInnerClass() {
        return this == CLASS_NESTED_INNER;
    }

    public boolean isNestedStaticClass() {
        return this == CLASS_NESTED_STATIC;
    }

    public boolean isNestedLocalClass() {
        return this == CLASS_NESTED_LOCAL;
    }

    public boolean isNestedAnonymousClass() {
        return this == CLASS_NESTED_ANONYMOUS;
    }

    public boolean isMethod() {
        return this == METHOD_BASE
                || this == METHOD_BLOCK
                || this == METHOD_STATIC_BLOCK
                || this == METHOD_CONSTRUCTOR;
    }

    public boolean isBaseMethod() {
        return this == METHOD_BASE;
    }

    public boolean isBlockMethod() {
        return this == METHOD_BLOCK;
    }

    public boolean isStaticBlockMethod() {
        return this == METHOD_STATIC_BLOCK;
    }

    public boolean isConstructorMethod() {
        return this == METHOD_CONSTRUCTOR;
    }

    public boolean isField() {
        return this == FIELD_BASE || this == FIELD_ENUM;
    }

    public boolean isBaseField() {
        return this == FIELD_BASE;
    }

    public boolean isEnumField() {
        return this == FIELD_ENUM;
    }

    public boolean isAttribute() {
        return this == ATTRIBUTE;
    }

    public boolean isParam() {
        return this == PARAM;
    }

}

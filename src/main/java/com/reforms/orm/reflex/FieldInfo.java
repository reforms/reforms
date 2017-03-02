package com.reforms.orm.reflex;

/**
 *
 * @author evgenie
 */
class FieldInfo {

    private final int constructorIndex;

    private final String fieldName;

    FieldInfo(int constructorIndex, String fieldName) {
        this.constructorIndex = constructorIndex;
        this.fieldName = fieldName;
    }

    int getConstructorIndex() {
        return constructorIndex;
    }

    String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "[constructorIndex=" + constructorIndex + ", fieldName=" + fieldName + "]";
    }
}

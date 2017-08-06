package com.reforms.cf.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Help for Attribute api
 *
 * @author evgenie
 */
public class Attributes implements Iterable<AttrBaseStruct> {

    public static final String UNKNOWN_ATTR = "Unknown";
    public static final String DEPRECATED_ATTR = "Deprecated";
    public static final String SYNTHETIC_ATTR = "Synthetic";
    public static final String ENCLOSING_METHOD_ATTR = "EnclosingMethod";
    public static final String EXCEPTIONS_ATTR = "Exceptions";
    public static final String CONSTANT_VALUE_ATTR = "ConstantValue";
    public static final String RUNTIME_VISIBLE_ANNOTATIONS_ATTR = "RuntimeVisibleAnnotations";
    public static final String RUNTIME_INVISIBLE_ANNOTATIONS_ATTR = "RuntimeInvisibleAnnotations";
    public static final String RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS_ATTR = "RuntimeVisibleParameterAnnotations";
    public static final String RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS_ATTR = "RuntimeInvisibleParameterAnnotations";
    public static final String RUNTIME_VISIBLE_TYPE_ANNOTATIONS_ATTR = "RuntimeVisibleTypeAnnotations";
    public static final String RUNTIME_INVISIBLE_TYPE_ANNOTATIONS_ATTR = "RuntimeInvisibleTypeAnnotations";
    public static final String ANNOTATION_DEFAULT_ATTR = "AnnotationDefault";
    public static final String METHOD_PARAMETERS_ATTR = "MethodParameters";
    public static final String BOOTSTRAP_METHODS_ATTR = "BootstrapMethods";
    public static final String CODE_ATTR = "Code";
    public static final String INNER_CLASSES_ATTR = "InnerClasses";
    public static final String SIGNATURE_ATTR = "Signature";
    public static final String SOURCE_FILE_ATTR = "SourceFile";
    public static final String SOURCE_DEBUG_EXTENSION_ATTR = "SourceDebugExtension";
    public static final String LINE_NUMBER_TABLE_ATTR = "LineNumberTable";
    public static final String LOCAL_VARIABLE_TABLE_ATTR = "LocalVariableTable";
    public static final String LOCAL_VARIABLE_TYPE_TABLE_ATTR = "LocalVariableTypeTable";
    public static final String STACK_MAP_TABLE_ATTR = "StackMapTable";

    public static boolean isConstantValue(String attrName) {
        return CONSTANT_VALUE_ATTR.equals(attrName);
    }

    public static boolean isCode(String attrName) {
        return CODE_ATTR.equals(attrName);
    }

    public static boolean isStackMapTable(String attrName) {
        return STACK_MAP_TABLE_ATTR.equals(attrName);
    }

    public static boolean isExceptions(String attrName) {
        return EXCEPTIONS_ATTR.equals(attrName);
    }

    public static boolean isInnerClasses(String attrName) {
        return INNER_CLASSES_ATTR.equals(attrName);
    }

    public static boolean isEnclosingMethod(String attrName) {
        return ENCLOSING_METHOD_ATTR.equals(attrName);
    }

    public static boolean isSynthetic(String attrName) {
        return SYNTHETIC_ATTR.equals(attrName);
    }

    public static boolean isSignature(String attrName) {
        return SIGNATURE_ATTR.equals(attrName);
    }

    public static boolean isSourceFile(String attrName) {
        return SOURCE_FILE_ATTR.equals(attrName);
    }

    public static boolean isSourceDebugExtension(String attrName) {
        return SOURCE_DEBUG_EXTENSION_ATTR.equals(attrName);
    }

    public static boolean isLineNumberTable(String attrName) {
        return LINE_NUMBER_TABLE_ATTR.equals(attrName);
    }

    public static boolean isLocalVariableTable(String attrName) {
        return LOCAL_VARIABLE_TABLE_ATTR.equals(attrName);
    }

    public static boolean isLocalVariableTypeTable(String attrName) {
        return LOCAL_VARIABLE_TYPE_TABLE_ATTR.equals(attrName);
    }

    public static boolean isDeprecated(String attrName) {
        return DEPRECATED_ATTR.equals(attrName);
    }

    public static boolean isRuntimeVisibleAnnotations(String attrName) {
        return RUNTIME_VISIBLE_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isRuntimeInvisibleAnnotations(String attrName) {
        return RUNTIME_INVISIBLE_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isRuntimeVisibleParameterAnnotations(String attrName) {
        return RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isRuntimeInvisibleParameterAnnotations(String attrName) {
        return RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isRuntimeVisibleTypeAnnotations(String attrName) {
        return RUNTIME_VISIBLE_TYPE_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isRuntimeInvisibleTypeAnnotations(String attrName) {
        return RUNTIME_INVISIBLE_TYPE_ANNOTATIONS_ATTR.equals(attrName);
    }

    public static boolean isAnnotationDefault(String attrName) {
        return ANNOTATION_DEFAULT_ATTR.equals(attrName);
    }

    public static boolean isMethodParameters(String attrName) {
        return METHOD_PARAMETERS_ATTR.equals(attrName);
    }

    public static boolean isBootstrapMethods(String attrName) {
        return BOOTSTRAP_METHODS_ATTR.equals(attrName);
    }

    private StructType ownerType;
    private  final AttrBaseStruct[] attributes;

    public Attributes(StructType ownerType, AttrBaseStruct[] attributes) {
        this.ownerType = ownerType;
        this.attributes = attributes;
    }

    public StructType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(StructType owner) {
        this.ownerType = owner;
    }

    public AttrBaseStruct[] getAttributes() {
        return attributes;
    }

    public <T extends AttrBaseStruct> T find(String attrName) {
        for (AttrBaseStruct attribute : this) {
            if (attribute.getName().equals(attrName)) {
                return (T) attribute;
            }
        }
        return null;
    }

    public <T extends AttrBaseStruct> List<T> finds(String attrName) {
        List<T> attrs = new ArrayList<T>();
        for (AttrBaseStruct attribute : this) {
            if (attribute.getName().equals(attrName)) {
                attrs.add((T) attribute);
            }
        }
        return attrs;
    }

    @Override
    public Iterator<AttrBaseStruct> iterator() {
        return Arrays.asList(attributes).iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attributes ").append(ownerType).append(" attributes=").append(Arrays.toString(attributes));
        return builder.toString();
    }



}

package com.reforms.cf.descriptor;

import java.util.HashMap;
import java.util.Map;


/**
 * Help for Descriptor api
 * @author evgenie
 *
 */
public class Descriptors {

    public static final char BYTE_TYPE = 'B'; // byte signed byte
    public static final char CHAR_TYPE = 'C'; // char Unicode character code point in the Basic Multilingual Plane, encoded with UTF-16
    public static final char DOUBLE_TYPE = 'D'; // double double-precision floating-point value
    public static final char FLOAT_TYPE = 'F'; // float single-precision floating-point value
    public static final char INT_TYPE = 'I'; // int integer
    public static final char LONG_TYPE = 'J'; // long long integer
    public static final char CLASS_NAME_TYPE = 'L'; // ClassName ; reference an instance of class ClassName
    public static final char SHORT_TYPE = 'S'; // short signed short
    public static final char BOOLEAN_TYPE = 'Z'; // boolean true or false
    public static final char VOID_TYPE = 'V'; // boolean true or false
    public static final char ARRAY_TYPE = '['; // reference one array dimension

    public static final Map<Character, String> TERM_NAMES = new HashMap<>();
    static {
        TERM_NAMES.put(BOOLEAN_TYPE, "boolean");
        TERM_NAMES.put(BYTE_TYPE, "byte");
        TERM_NAMES.put(SHORT_TYPE, "short");
        TERM_NAMES.put(CHAR_TYPE, "char");
        TERM_NAMES.put(FLOAT_TYPE, "float");
        TERM_NAMES.put(INT_TYPE, "int");
        TERM_NAMES.put(DOUBLE_TYPE, "double");
        TERM_NAMES.put(LONG_TYPE, "long");
        TERM_NAMES.put(CLASS_NAME_TYPE, "ClassName");
        TERM_NAMES.put(VOID_TYPE, "void");
        TERM_NAMES.put(ARRAY_TYPE, "array");
    }

    public static String getTermName(char term) {
        return TERM_NAMES.get(term);
    }

    public static final BaseType BYTE = new BaseType(BYTE_TYPE);
    public static final BaseType CHAR = new BaseType(CHAR_TYPE);
    public static final BaseType DOUBLE = new BaseType(DOUBLE_TYPE);
    public static final BaseType FLOAT = new BaseType(FLOAT_TYPE);
    public static final BaseType INT = new BaseType(INT_TYPE);
    public static final BaseType LONG = new BaseType(LONG_TYPE);
    public static final BaseType SHORT = new BaseType(SHORT_TYPE);
    public static final BaseType BOOLEAN = new BaseType(BOOLEAN_TYPE);
    public static final VoidType VOID = new VoidType();

    public static final Map<Character, BaseType> BASE_TERMS = new HashMap<>();
    static {
        BASE_TERMS.put(BYTE_TYPE, BYTE);
        BASE_TERMS.put(CHAR_TYPE, CHAR);
        BASE_TERMS.put(DOUBLE_TYPE, DOUBLE);
        BASE_TERMS.put(FLOAT_TYPE, FLOAT);
        BASE_TERMS.put(INT_TYPE, INT);
        BASE_TERMS.put(LONG_TYPE, LONG);
        BASE_TERMS.put(SHORT_TYPE, SHORT);
        BASE_TERMS.put(BOOLEAN_TYPE, BOOLEAN);
    }

    public static boolean isBaseTerm(char term) {
        return BASE_TERMS.containsKey(term);
    }

    public static BaseType getBaseTerm(char term) {
        return BASE_TERMS.get(term);
    }

    public static boolean isVoidTerm(char term) {
        return term == VOID_TYPE;
    }

    public static boolean isObjectTerm(char term) {
        return CLASS_NAME_TYPE == term;
    }

    public static boolean isArrayTerm(char term) {
        return ARRAY_TYPE == term;
    }

    public static boolean isCategory2(FieldDescriptor descriptor) {
        return descriptor != null && (descriptor.getTerm() == LONG_TYPE || descriptor.getTerm() == DOUBLE_TYPE);
    }

    public static boolean isCategory1(FieldDescriptor descriptor) {
        return descriptor != null && !isCategory2(descriptor);
    }

    public static final ObjectType STRING = new ObjectType("java/lang/String", "String");
    public static final ObjectType OBJECT = new ObjectType("java/lang/Object", "Object");
    public static final ObjectType ENUM = new ObjectType("java/lang/Enum", "Enum");
    public static final ObjectType CLASS = new ObjectType("java/lang/Class", "Class");
    public static final ObjectType THROWABLE = new ObjectType("java/lang/Throwable", "Throwable");
    public static final ObjectType MAP = new ObjectType("java/util/Map", "Map");
    public static final ObjectType LIST = new ObjectType("java/util/List", "List");
    public static final ObjectType SET = new ObjectType("java/util/Set", "Set");
    public static final ObjectType FILE = new ObjectType("java/io/File", "File");
    public static final ObjectType METHOD_TYPE = new ObjectType("java/lang/invoke/MethodType", "MethodType");
    public static final ObjectType METHOD_HANDLE = new ObjectType("java/lang/invoke/MethodHandle", "MethodHandle");
    public static final ObjectType METHOD_HANDLES = new ObjectType("java/lang/invoke/MethodHandles", "MethodHandles");
    public static final ObjectType METHOD_HANDLES_LOOKUP = new ObjectType("java/lang/invoke/MethodHandles$Lookup", "Lookup");
    public static final ObjectType BOOLEAN_W = new ObjectType("java/lang/Boolean", "Boolean");
    public static final ObjectType PRINT_STREAM = new ObjectType("java/io/PrintStream", "PrintStream");
    public static final ArrayType A_BYTE = new ArrayType(BYTE, BYTE, 1);
    public static final ArrayType A_INT = new ArrayType(INT, INT, 1);
    public static final ArrayType A_CHAR = new ArrayType(CHAR, CHAR, 1);
    public static final ArrayType AA_CHAR = new ArrayType(A_CHAR, CHAR, 2);
    public static final ArrayType A_STRING = new ArrayType(STRING, STRING, 1);

    public static final ObjectType BYTE_REF = new ObjectType("java/lang/Byte", "Byte");
    public static final ObjectType CHAR_REF = new ObjectType("java/lang/Character", "Character");
    public static final ObjectType DOUBLE_REF = new ObjectType("java/lang/Double", "Double");
    public static final ObjectType FLOAT_REF = new ObjectType("java/lang/Float", "Float");
    public static final ObjectType INT_REF = new ObjectType("java/lang/Integer", "Integer");
    public static final ObjectType LONG_REF = new ObjectType("java/lang/Long", "Long");
    public static final ObjectType SHORT_REF = new ObjectType("java/lang/Short", "Short");
    public static final ObjectType BOOLEAN_REF = new ObjectType("java/lang/Boolean", "Boolean");


    private static final Map<String, FieldDescriptor> OFTEN_FIELD_DESCRIPTORS = new HashMap<String, FieldDescriptor>();
    static {
        for (FieldDescriptor desriptor : BASE_TERMS.values()) {
            OFTEN_FIELD_DESCRIPTORS.put(desriptor.getDescriptor(), desriptor);
        }
        OFTEN_FIELD_DESCRIPTORS.put(VOID.getDescriptor(), VOID);
        OFTEN_FIELD_DESCRIPTORS.put(STRING.getDescriptor(), STRING);
        OFTEN_FIELD_DESCRIPTORS.put(OBJECT.getDescriptor(), OBJECT);
        OFTEN_FIELD_DESCRIPTORS.put(ENUM.getDescriptor(), ENUM);
        OFTEN_FIELD_DESCRIPTORS.put(CLASS.getDescriptor(), CLASS);
        OFTEN_FIELD_DESCRIPTORS.put(MAP.getDescriptor(), MAP);
        OFTEN_FIELD_DESCRIPTORS.put(SET.getDescriptor(), SET);
        OFTEN_FIELD_DESCRIPTORS.put(LIST.getDescriptor(), LIST);
        OFTEN_FIELD_DESCRIPTORS.put(FILE.getDescriptor(), FILE);
        OFTEN_FIELD_DESCRIPTORS.put(METHOD_TYPE.getDescriptor(), METHOD_TYPE);
        OFTEN_FIELD_DESCRIPTORS.put(METHOD_HANDLE.getDescriptor(), METHOD_HANDLE);
        OFTEN_FIELD_DESCRIPTORS.put(METHOD_HANDLES.getDescriptor(), METHOD_HANDLES);
        OFTEN_FIELD_DESCRIPTORS.put(METHOD_HANDLES_LOOKUP.getDescriptor(), METHOD_HANDLES_LOOKUP);
        OFTEN_FIELD_DESCRIPTORS.put(BOOLEAN_W.getDescriptor(), BOOLEAN_W);
        OFTEN_FIELD_DESCRIPTORS.put(PRINT_STREAM.getDescriptor(), PRINT_STREAM);
        OFTEN_FIELD_DESCRIPTORS.put(A_BYTE.getDescriptor(), A_BYTE);
        OFTEN_FIELD_DESCRIPTORS.put(A_CHAR.getDescriptor(), A_CHAR);
        OFTEN_FIELD_DESCRIPTORS.put(AA_CHAR.getDescriptor(), AA_CHAR);
        OFTEN_FIELD_DESCRIPTORS.put(A_INT.getDescriptor(), A_INT);
        OFTEN_FIELD_DESCRIPTORS.put(A_STRING.getDescriptor(), A_STRING);
        OFTEN_FIELD_DESCRIPTORS.put(BYTE_REF.getDescriptor(), BYTE_REF);
        OFTEN_FIELD_DESCRIPTORS.put(CHAR_REF.getDescriptor(), CHAR_REF);
        OFTEN_FIELD_DESCRIPTORS.put(DOUBLE_REF.getDescriptor(), DOUBLE_REF);
        OFTEN_FIELD_DESCRIPTORS.put(FLOAT_REF.getDescriptor(), FLOAT_REF);
        OFTEN_FIELD_DESCRIPTORS.put(INT_REF.getDescriptor(), INT_REF);
        OFTEN_FIELD_DESCRIPTORS.put(LONG_REF.getDescriptor(), LONG_REF);
        OFTEN_FIELD_DESCRIPTORS.put(SHORT_REF.getDescriptor(), SHORT_REF);
        OFTEN_FIELD_DESCRIPTORS.put(BOOLEAN_REF.getDescriptor(), BOOLEAN_REF);
    }

    public static FieldDescriptor getFieldDescriptor(String descriptor) {
        return OFTEN_FIELD_DESCRIPTORS.get(descriptor);
    }

    private static final String METHOD_TYPE_DESC = "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;";
    private static final String METHOD_LOOKUP_DESC = "()Ljava/lang/invoke/MethodHandles$Lookup;";
    private static final String METHOD_HANDLE_DESC = "()Ljava/lang/invoke/MethodHandle;";
    private static final String METHOD_HANDLE_V1 = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;";
    private static final String METHOD_HANDLE_V2 = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;";
    private static final String METHOD_HANDLE_V3 = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;";
    private static final String METHOD_HANDLE_V4 = "(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;";

    private static final Map<String, MethodDescriptor> OFTEN_METHOD_DESCRIPTORS = new HashMap<String, MethodDescriptor>();

    public static final MethodDescriptor METHOD_TYPE_DESCRIPTOR = getMethodDescriptor(METHOD_TYPE_DESC);
    public static final MethodDescriptor METHOD_HANDLE_DESCRIPTOR = getMethodDescriptor(METHOD_HANDLE_DESC);
    public static final MethodDescriptor METHOD_LOOKUP_DESCRIPTOR = getMethodDescriptor(METHOD_LOOKUP_DESC);
    public static final MethodDescriptor METHOD_HANDLE_V1_DESCRIPTOR = getMethodDescriptor(METHOD_HANDLE_V1);
    public static final MethodDescriptor METHOD_HANDLE_V2_DESCRIPTOR = getMethodDescriptor(METHOD_HANDLE_V2);
    public static final MethodDescriptor METHOD_HANDLE_V3_DESCRIPTOR = getMethodDescriptor(METHOD_HANDLE_V3);
    public static final MethodDescriptor METHOD_HANDLE_V4_DESCRIPTOR = getMethodDescriptor(METHOD_HANDLE_V4);


    public static MethodDescriptor getMethodDescriptor(String descriptor) {
        return OFTEN_METHOD_DESCRIPTORS.get(descriptor);
    }

    public static boolean checkDescriptorEquals(FieldDescriptor descriptor1, FieldDescriptor descriptor2) {
        if (descriptor1 == descriptor2) {
            return true;
        }
        return descriptor1 != null && descriptor2 != null && descriptor1.getDescriptor().equals(descriptor2.getDescriptor());
    }

    public static boolean checkDescriptorEquals(MethodDescriptor descriptor1, MethodDescriptor descriptor2) {
        if (descriptor1 == descriptor2) {
            return true;
        }
        return descriptor1 != null && descriptor2 != null && descriptor1.getDescriptor().equals(descriptor2.getDescriptor());
    }

}

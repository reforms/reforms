package com.reforms.cf.struct;

/**
 * Help for TypePathe api
 * @author evgenie
 *
 */
public class TypePathes {

    public static final int DEEPER_ARRAY_PATH_KIND = 0; // Annotation is deeper in an array type
    public static final int DEEPER_IN_NESTED_ARRAY_PATH_KIND = 1; // Annotation is deeper in a nested type
    public static final int WILDCARD_PARAMETERIZED_TYPE_PATH_KIND = 2; // Annotation is on the bound of a wildcard type argument of a parameterized type
    public static final int PARAMETERIZED_TYPE_PATH_KIND = 3; // Annotation is on a type argument of a parameterized type


}

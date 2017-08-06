package com.reforms.cf.struct;

/**
 * Help for Method api
 * @author evgenie
 */
public class Methods {

    public static final String CONSTUCTOR_NAME = "<init>";
    public static final String STATIC_BLOCK_NAME = "<clinit>";

    public static boolean isConstructor(String methodName) {
        return CONSTUCTOR_NAME.equals(methodName);
    }

    public static boolean isStaticBlock(String methodName) {
        return STATIC_BLOCK_NAME.equals(methodName);
    }
}

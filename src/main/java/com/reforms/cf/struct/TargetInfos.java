package com.reforms.cf.struct;

/**
 * Help for TargetInfo api
 * @author evgenie
 */
public class TargetInfos {

    public static final int TYPE_PARAMETER_TARGET_CI = 0x00; // type parameter declaration of generic class type_parameter_target or interface
    public static final int TYPE_PARAMETER_TARGET_MC = 0x01; // type parameter declaration of generic type_parameter_target method or constructor
    public static final int SUPERTYPE_TARGET = 0x10; // type in extends clause of class supertype_target or interface declaration (including the direct superclass of an anonymous class declaration), or in implements clause of interface declaration
    public static final int TYPE_PARAMETER_BOUND_TARGET_CI = 0x11; // type in bound of type parameter declaration type_parameter_bound_target of generic class or interface
    public static final int TYPE_PARAMETER_BOUND_TARGET_MC = 0x12; // type in bound of type parameter declaration type_parameter_bound_target of generic method or constructor
    public static final int EMPTY_TARGET_FIELD = 0x13; // type in field declaration empty_target
    public static final int EMPTY_TARGET_RETURN_MC = 0x14; // type return type of method, or type of newly empty_target constructed object
    public static final int EMPTY_TARGET_RECIEVE_MC = 0x15; // type receiver type of method or constructor empty_target
    public static final int FORMAL_PARAMETER_TARGET_MC = 0x16; // type in formal parameter declaration offormal_parameter_target method, constructor, or lambda expression
    public static final int THROWS_TARGET = 0x17; // type in throws clause of method or throws_target constructor
    public static final int LOCALVAR_TARGET = 0x40; // type in local variable declaration localvar_target
    public static final int LOCALVAR_TARGET_RESOURCE = 0x41; // type in resource variable declaration localvar_target
    public static final int CATCH_TARGET = 0x42; // type in exception parameter declaration catch_target
    public static final int OFFSET_TARGET_INSTANCEOF = 0x43; // type in instanceof expression offset_target
    public static final int OFFSET_TARGET_NEW = 0x44; // type in new expression offset_target
    public static final int OFFSET_TARGET_MRE_NEW = 0x45; // type in method reference expression using ::new offset_target
    public static final int OFFSET_TARGET_MRE_IDENTIFIER = 0x46; // type in method reference expression using offset_target ::Identifier
    public static final int TYPE_ARGUMENT_TARGET_CAST = 0x47; // type in cast expression type_argument_target
    public static final int TYPE_ARGUMENT_TARGET_CONSTRUCTOR = 0x48; // type argument for generic constructor in new type_argument_target expression or explicit constructor invocation statement
    public static final int TYPE_ARGUMENT_TARGET_MRE = 0x49; // type argument for generic method in method type_argument_target invocation expression
    public static final int TYPE_ARGUMENT_TARGET_MRE_NEW= 0x4A; // type argument for generic constructor in method type_argument_target reference expression using ::new
    public static final int TYPE_ARGUMENT_TARGET_MRE_IDENTIFIER = 0x4B; // type argument for generic method in method type_argument_target reference expression using ::Identifier

    public static final String TYPE_PARAMETER_TARGET_NAME = "TYPE_PARAMETER_TARGET";
    public static final String SUPERTYPE_TARGET_NAME = "SUPERTYPE_TARGET";
    public static final String TYPE_PARAMETER_BOUND_TARGET_NAME = "TYPE_PARAMETER_BOUND_TARGET";
    public static final String EMPTY_TARGET_NAME = "EMPTY_TARGET";
    public static final String FORMAL_PARAMETER_TARGET_NAME = "FORMAL_PARAMETER_TARGET";
    public static final String THROWS_TARGET_NAME = "THROWS_TARGET";
    public static final String LOCALVAR_TARGET_NAME = "LOCALVAR_TARGET";
    public static final String CATCH_TARGET_NAME = "CATCH_TARGET";
    public static final String OFFSET_TARGET_NAME = "OFFSET_TARGET";
    public static final String TYPE_ARGUMENT_TARGET_NAME = "TYPE_ARGUMENT_TARGET";

    public static boolean isTypeParameterTarget(int tag) {
        return TYPE_PARAMETER_TARGET_CI == tag || TYPE_PARAMETER_TARGET_MC == tag;
    }

    public static boolean isSupertypeTarget(int tag) {
        return SUPERTYPE_TARGET == tag;
    }

    public static boolean isTypeParameterBoundTarget(int tag) {
        return TYPE_PARAMETER_BOUND_TARGET_CI == tag || TYPE_PARAMETER_BOUND_TARGET_MC == tag;
    }

    public static boolean isEmptyTarget(int tag) {
        return EMPTY_TARGET_FIELD == tag || EMPTY_TARGET_RETURN_MC == tag || EMPTY_TARGET_RECIEVE_MC == tag;
    }

    public static boolean isFormalParameterTarget(int tag) {
        return FORMAL_PARAMETER_TARGET_MC == tag;
    }

    public static boolean isThrowsTarget(int tag) {
        return THROWS_TARGET == tag;
    }

    public static boolean isLocalvarTarget(int tag) {
        return LOCALVAR_TARGET == tag || LOCALVAR_TARGET_RESOURCE == tag;
    }

    public static boolean isCatchTarget(int tag) {
        return CATCH_TARGET == tag;
    }

    public static boolean isOffsetTarget(int tag) {
        return OFFSET_TARGET_INSTANCEOF == tag || OFFSET_TARGET_NEW == tag
                || OFFSET_TARGET_MRE_NEW == tag || OFFSET_TARGET_MRE_IDENTIFIER == tag;
    }

    public static boolean isTypeArgumentTarget(int tag) {
        return TYPE_ARGUMENT_TARGET_CAST == tag || TYPE_ARGUMENT_TARGET_CONSTRUCTOR == tag
                || TYPE_ARGUMENT_TARGET_MRE == tag || TYPE_ARGUMENT_TARGET_MRE_NEW == tag
                || TYPE_ARGUMENT_TARGET_MRE_IDENTIFIER == tag;
    }

}

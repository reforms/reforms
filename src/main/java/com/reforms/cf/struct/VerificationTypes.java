package com.reforms.cf.struct;

/**
 * Help for VerificationTypeInfo api
 *
 * @author evgenie
 *
 */
public class VerificationTypes {

    public static final int TOP_VARIABLE_TAG = 0;
    public static final int INTEGER_VARIABLE_TAG = 1;
    public static final int FLOAT_VARIABLE_TAG = 2;
    public static final int LONG_VARIABLE_TAG = 3;
    public static final int DOUBLE_VARIABLE_TAG = 4;
    public static final int NULL_VARIABLE_TAG = 5;
    public static final int UNINITIALIZED_THIS_VARIABLE_TAG = 6;
    public static final int OBJECT_VARIABLE_TAG = 7;
    public static final int UNINITIALIZED_VARIABLE_TAG = 8;

    public static final String TOP_VARIABLE_NAME = "Top_variable";
    public static final String INTEGER_VARIABLE_NAME = "Integer_variable";
    public static final String FLOAT_VARIABLE_NAME = "Float_variable";
    public static final String LONG_VARIABLE_NAME = "Long_variable";
    public static final String DOUBLE_VARIABLE_NAME = "Double_variable";
    public static final String NULL_VARIABLE_NAME = "Null_variable";
    public static final String UNINITIALIZED_THIS_VARIABLE_NAME = "UninitializedThis_variable";
    public static final String OBJECT_VARIABLE_NAME = "Object_variable";
    public static final String UNINITIALIZED_VARIABLE_NAME = "Uninitialized_variable";

    public static final VerificationTypeTopVariableStruct TOP_VARIABLE = new VerificationTypeTopVariableStruct();
    public static final VerificationTypeIntegerVariableStruct INTEGER_VARIABLE = new VerificationTypeIntegerVariableStruct();
    public static final VerificationTypeFloatVariableStruct FLOAT_VARIABLE = new VerificationTypeFloatVariableStruct();
    public static final VerificationTypeLongVariableStruct LONG_VARIABLE = new VerificationTypeLongVariableStruct();
    public static final VerificationTypeDoubleVariableStruct DOUBLE_VARIABLE = new VerificationTypeDoubleVariableStruct();
    public static final VerificationTypeNullVariableStruct NULL_VARIABLE = new VerificationTypeNullVariableStruct();
    public static final VerificationTypeUninitializedThisVariableStruct UNINITIALIZED_THIS_VARIABLE = new VerificationTypeUninitializedThisVariableStruct();
}

package com.reforms.cf.struct;

import com.reforms.cf.ClassFormatException;
import com.reforms.cf.ExceptionBuilder;

/**
 * Help for ConstantPool api
 * @author evgenie
 */
public class ConstantPool {

    private static final String UNKNOWN_TAG = "UNKNOWN_TAG";
    private static final String NULLABLE_TAG_NAME = "NULLABLE_TAG_NAME";
    private static final CpItemStruct NULLABLE = new CpNullableStruct();

    public static final int NULLABLE_TAG = 0; // synthetic
    public static final int CLASS_TAG = 7;
    public static final int FIELD_REF_TAG = 9;
    public static final int METHOD_REF_TAG = 10;
    public static final int INTERFACE_METHOD_REF_TAG = 11;
    public static final int STRING_TAG = 8;
    public static final int INTEGER_TAG = 3;
    public static final int FLOAT_TAG = 4;
    public static final int LONG_TAG = 5;
    public static final int DOUBLE_TAG = 6;
    public static final int NAME_AND_TYPE_TAG = 12;
    public static final int UTF8_TAG = 1;
    public static final int METHOD_HANDLE_TAG = 15;
    public static final int METHOD_TYPE_TAG = 16;
    public static final int INVOKE_DYNAMIC_TAG = 18;

    private static String[] TAG_NAMES = new String[] {
        NULLABLE_TAG_NAME, // 0
        "UTF8_TAG", // 1
        UNKNOWN_TAG, // 2
        "INTEGER_TAG", // 3
        "FLOAT_TAG", // 4
        "LONG_TAG", // 5
        "DOUBLE_TAG", // 6
        "CLASS_TAG", // 7
        "STRING_TAG", // 8
        "FIELD_REF_TAG", // 9
        "METHOD_REF_TAG", // 10
        "INTERFACE_METHOD_REF_TAG", // 11
        "NAME_AND_TYPE_TAG", // 12
        UNKNOWN_TAG, // 13
        UNKNOWN_TAG, // 14
        "METHOD_HANDLE_TAG", // 15
        "METHOD_TYPE_TAG", // 16
        UNKNOWN_TAG, // 17
        "INVOKE_DYNAMIC_TAG", // 18
        UNKNOWN_TAG, // 19
        UNKNOWN_TAG, // 20
    };

    public static String getTagName(int tag) {
        if (tag > -1 && tag < TAG_NAMES.length) {
            return TAG_NAMES[tag];
        }
        return UNKNOWN_TAG;
    }

    private final CpItemStruct[] cpItems;

    public ConstantPool(CpItemStruct[] cpItems) {
        this.cpItems = cpItems;
    }

    public CpItemStruct[] getCpItems() {
        return cpItems;
    }

    public String getStringValue(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (UTF8_TAG == cpItem.getTag()) {
            return ((CpUtf8Struct) cpItem).getValue();
        }
        if (STRING_TAG == cpItem.getTag()) {
            return getStringValue(((CpStringStruct) cpItem).getStringIndex());
        }
        throw makeIncorrectItemException(index, String.class);
    }

    public int getIntValue(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (INTEGER_TAG == cpItem.getTag()) {
            return ((CpIntegerStruct) cpItem).getValue();
        }

        throw makeIncorrectItemException(index, int.class);
    }

    public byte getByteValue(int index) {
        return (byte) getIntValue(index);
    }

    public char getCharValue(int index) {
        return (char) getIntValue(index);
    }

    public short getShortValue(int index) {
        return (short) getIntValue(index);
    }

    public boolean getBooleanValue(int index) {
        return getIntValue(index) == 1;
    }

    public double getDoubleValue(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (DOUBLE_TAG == cpItem.getTag()) {
            return ((CpDoubleStruct) cpItem).getValue();
        }

        throw makeIncorrectItemException(index, double.class);
    }

    public float getFloatValue(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (FLOAT_TAG == cpItem.getTag()) {
            return ((CpFloatStruct) cpItem).getValue();
        }

        throw makeIncorrectItemException(index, float.class);
    }

    public long getLongValue(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (LONG_TAG == cpItem.getTag()) {
            return ((CpLongStruct) cpItem).getValue();
        }

        throw makeIncorrectItemException(index, long.class);
    }

    public String getBinnaryClassName(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (CLASS_TAG == cpItem.getTag()) {
            int nameIndex = ((CpClassStruct) cpItem).getNameIndex();
            return getStringValue(nameIndex);
        }

        throw makeIncorrectItemException(index, CpClassStruct.class);
    }

    public CpInvokeDynamicStruct getInvokeDynamic(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (INVOKE_DYNAMIC_TAG == cpItem.getTag()) {
            return ((CpInvokeDynamicStruct) cpItem);
        }

        throw makeIncorrectItemException(index, CpInvokeDynamicStruct.class);
    }

    public CpNameAndTypeStruct getNameAndType(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (NAME_AND_TYPE_TAG == cpItem.getTag()) {
            return ((CpNameAndTypeStruct) cpItem);
        }
        if (FIELD_REF_TAG == cpItem.getTag()
                || METHOD_REF_TAG == cpItem.getTag()
                || INTERFACE_METHOD_REF_TAG == cpItem.getTag()) {
            int nameAndTypeIndex = ((CpMemberRefStruct) cpItem).getNameAndTypeIndex();
            return getNameAndType(nameAndTypeIndex);
        }

        throw makeIncorrectItemException(index, CpNameAndTypeStruct.class);
    }

    public CpMethodTypeStruct getMethodType(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (METHOD_TYPE_TAG == cpItem.getTag()) {
            return ((CpMethodTypeStruct) cpItem);
        }
        throw makeIncorrectItemException(index, CpMethodTypeStruct.class);
    }

    public CpMethodHandleStruct getMethodHandle(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (METHOD_HANDLE_TAG == cpItem.getTag()) {
            return ((CpMethodHandleStruct) cpItem);
        }
        throw makeIncorrectItemException(index, CpMethodHandleStruct.class);
    }

    public CpMemberRefStruct getMemberRef(int index) {
        CpItemStruct cpItem = getCpItem(index);
        if (FIELD_REF_TAG == cpItem.getTag()
                || METHOD_REF_TAG == cpItem.getTag()
                || INTERFACE_METHOD_REF_TAG == cpItem.getTag()) {
            return ((CpMemberRefStruct) cpItem);
        }
        throw makeIncorrectItemException(index, CpMemberRefStruct.class);
    }

    public CpItemStruct getCpItem(int index) {
        if (index < 1 || index >= cpItems.length || cpItems[index] == null) {
            return NULLABLE;
        }
        return cpItems[index];
    }

    private ClassFormatException makeIncorrectItemException(int index, Class<?> clazz) {
        CpItemStruct cpItem = getCpItem(index);
        throw new ExceptionBuilder()
            .area(ClassFormatException.CONSTANT_POOL_AREA)
            .reason("Структура {0} с индексом {1} не содержит значения типа {2}", cpItem, index, clazz)
            .effect("Невозможно получить строковое значение из {0}", getTagName(cpItem.getTag()))
            .action("Необходимо доработать логику запроса строкового значения в месте вызова")
            .exception();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConstantPool\n");
        for (int index = 1; index < cpItems.length; index++) {
            builder.append("    " + index + ". " + getCpItem(index) + "\n");
        }
        return builder.toString();
    }



}

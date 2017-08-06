package com.reforms.cf.descriptor;

import static com.reforms.cf.descriptor.Descriptors.*;



/**
 * Help for ArrayType api
 * @author evgenie
 */
public class ArrayTypes {

    private static final ArrayType[] ARRAY_TYPES = new ArrayType[] {
        null,               // 0
        null,               // 1
        null,               // 2
        null,               // 3
        arrayFrom(BOOLEAN), // 4  - T_BOOLEAN
        arrayFrom(CHAR),    // 5  - T_CHAR
        arrayFrom(FLOAT),   // 6  - T_FLOAT
        arrayFrom(DOUBLE),  // 7  - T_DOUBLE
        arrayFrom(BYTE),    // 8  - T_BYTE
        arrayFrom(SHORT),   // 9  - T_SHORT
        arrayFrom(INT),     // 10 - T_INT
        arrayFrom(LONG)     // 11 - T_LONG
    };

    public static ArrayType arrayFrom(FieldDescriptor fieldDescriptor) {
        return arrayFrom(fieldDescriptor, 1);
    }

    public static ArrayType arrayFrom(FieldDescriptor fieldDescriptor, int dimension) {
        String arrayDescriptor = ARRAY_TYPE + fieldDescriptor.getDescriptor();
        FieldDescriptor resultType = DescriptorParser.parseFieldDescriptor(arrayDescriptor);
        if (dimension == 1) {
            return (ArrayType) resultType;
        }
        return arrayFrom(resultType, --dimension);
    }

    public static ArrayType getArrayType(int index) {
        return ARRAY_TYPES[index];
    }
}

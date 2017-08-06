package com.reforms.cf.struct;

/**
 * The NULLABLE synthetic struct
 *
 * @author evgenie
 */
public class CpNullableStruct extends CpItemStruct {

    public CpNullableStruct() {
        super(ConstantPool.NULLABLE_TAG);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_NULLABLE_TAG = ").append(getTag());
        return builder.toString();
    }

}

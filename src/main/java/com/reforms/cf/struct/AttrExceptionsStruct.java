package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The Exceptions Attribute
 *
 * @author evgenie
 */
public class AttrExceptionsStruct extends AttrBaseStruct {

    private int numberOfExceptions; // u2
    private int[] exceptionIndexTable; // u2Array

    public AttrExceptionsStruct() {
        super(Attributes.EXCEPTIONS_ATTR);
    }

    public int getNumberOfExceptions() {
        return numberOfExceptions;
    }

    public void setNumberOfExceptions(int numberOfExceptions) {
        this.numberOfExceptions = numberOfExceptions;
    }

    public int[] getExceptionIndexTable() {
        return exceptionIndexTable;
    }

    public void setExceptionIndexTable(int[] exceptionIndexTable) {
        this.exceptionIndexTable = exceptionIndexTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numberOfExceptions=").append(numberOfExceptions)
                .append(", exceptionIndexTable=").append(Arrays.toString(exceptionIndexTable));
        return builder.toString();
    }

}

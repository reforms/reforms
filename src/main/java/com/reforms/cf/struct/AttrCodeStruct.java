package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The Code Attribute
 *
 * @author evgenie
 */
public class AttrCodeStruct extends AttrBaseStruct {

    private int maxStack; // u2
    private int maxLocals; // u2
    private int codeLength; // u4
    private byte[] code; // u1Array
    private int exceptionTableLength; // u2
    private ExceptionRecordStruct[] exceptionTable;
    private int attributesCount; // u2
    private Attributes attributes;

    public AttrCodeStruct() {
        super(Attributes.CODE_ATTR);
    }

    public int getMaxStack() {
        return maxStack;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public int getExceptionTableLength() {
        return exceptionTableLength;
    }

    public void setExceptionTableLength(int exceptionTableLength) {
        this.exceptionTableLength = exceptionTableLength;
    }

    public ExceptionRecordStruct[] getExceptionTable() {
        return exceptionTable;
    }

    public void setExceptionTable(ExceptionRecordStruct[] exceptionTable) {
        this.exceptionTable = exceptionTable;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public void setAttributesCount(int attributesCount) {
        this.attributesCount = attributesCount;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" maxStack=").append(maxStack)
                .append(", maxLocals=").append(maxLocals).append(", codeLength=").append(codeLength)
                .append(", exceptionTableLength=").append(exceptionTableLength).append(", exceptionTable=")
                .append(Arrays.toString(exceptionTable)).append(", attributesCount=").append(attributesCount)
                .append(", attributes=").append(attributes);
        return builder.toString();
    }

}

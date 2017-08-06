package com.reforms.cf.struct;

/**
 * The CONSTANT_MethodHandle_info Structure
 *
 * @author evgenie
 */
public class CpMethodHandleStruct extends CpItemStruct {

    private int referenceKind; // u1
    private int referenceIndex; // u2

    public CpMethodHandleStruct() {
        super(ConstantPool.METHOD_HANDLE_TAG);
    }

    public int getReferenceKind() {
        return referenceKind;
    }

    public void setReferenceKind(int referenceKind) {
        this.referenceKind = referenceKind;
    }

    public int getReferenceIndex() {
        return referenceIndex;
    }

    public void setReferenceIndex(int referenceIndex) {
        this.referenceIndex = referenceIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CP_METHOD_HANDLE_TAG = ").append(getTag()).append(", referenceKind=").append(referenceKind)
                .append(", referenceIndex=").append(referenceIndex).append("]");
        return builder.toString();
    }

}

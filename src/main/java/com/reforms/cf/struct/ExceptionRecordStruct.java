package com.reforms.cf.struct;

/**
 * The ExceptionRecord struct
 *
 * @author evgenie
 *
 */
public class ExceptionRecordStruct {
    private int startPc; // u2
    private int endPc; // u2
    private int handlerPc; // u2
    private int catchType; // u2

    public int getStartPc() {
        return startPc;
    }

    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    public int getEndPc() {
        return endPc;
    }

    public void setEndPc(int endPc) {
        this.endPc = endPc;
    }

    public int getHandlerPc() {
        return handlerPc;
    }

    public void setHandlerPc(int handlerPc) {
        this.handlerPc = handlerPc;
    }

    public int getCatchType() {
        return catchType;
    }

    public void setCatchType(int catchType) {
        this.catchType = catchType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExceptionTable startPc=").append(startPc).append(", endPc=").append(endPc)
                .append(", handlerPc=").append(handlerPc).append(", catchType=").append(catchType);
        return builder.toString();
    }

}

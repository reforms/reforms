package com.reforms.orm.reflex;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

public enum BobjType {

    SIMPLE(0), HARD(1);

    @TargetField
    private int code;

    private BobjType(int code) {
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    @TargetMethod
    public static BobjType findBobjType(int code) {
        return SIMPLE.code == code ? SIMPLE : HARD;
    }
}

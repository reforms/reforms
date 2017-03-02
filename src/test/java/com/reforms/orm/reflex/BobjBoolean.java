package com.reforms.orm.reflex;

/**
 *
 * @author evgenie
 */
public class BobjBoolean {

    private boolean bool0 = false;

    private boolean bool1 = false;

    private boolean boolF = false;

    private boolean boolT = true;

    private boolean bool2 = true;

    private boolean bool3 = true;

    public BobjBoolean(boolean f, boolean t) {
        boolF = f;
        boolT = t;
    }

    public boolean isBool0() {
        return bool0;
    }

    public void setBool0(boolean bool0) {
        this.bool0 = bool0;
    }

    public boolean isBool1() {
        return bool1;
    }

    public void setBool1(boolean bool1) {
        this.bool1 = bool1;
    }

    public boolean isBoolF() {
        return boolF;
    }

    public void setBoolF(boolean boolF) {
        this.boolF = boolF;
    }

    public boolean isBoolT() {
        return boolT;
    }

    public void setBoolT(boolean boolT) {
        this.boolT = boolT;
    }

    public boolean isBool2() {
        return bool2;
    }

    public void setBool2(boolean bool2) {
        this.bool2 = bool2;
    }

    public boolean isBool3() {
        return bool3;
    }

    public void setBool3(boolean bool3) {
        this.bool3 = bool3;
    }

    @Override
    public String toString() {
        return "[bool0=" + bool0 + ", bool1=" + bool1 + ", boolF=" + boolF + ", boolT=" + boolT + ", bool2=" + bool2 + ", bool3=" + bool3 + "]";
    }

}

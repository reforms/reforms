package com.reforms.orm.reflex;

/**
 *
 * @author evgenie
 */
public class BobjInt {

    private int int0 = 0;

    private int int1 = 1;

    private int int2 = 2;

    private int int3 = 3;

    private int int4 = 4;

    public BobjInt(int int1, int int2, int int3) {
        this.int1 = int1;
        this.int2 = int2;
        this.int3 = int3;
    }

    public int getInt0() {
        return int0;
    }

    public void setInt0(int int0) {
        this.int0 = int0;
    }

    public int getInt1() {
        return int1;
    }

    public int getInt2() {
        return int2;
    }

    public int getInt3() {
        return int3;
    }

    public int getInt4() {
        return int4;
    }

    public void setInt4(int int4) {
        this.int4 = int4;
    }

    @Override
    public String toString() {
        return "[int0=" + int0 + ", int1=" + int1 + ", int2=" + int2 + ", int3=" + int3 + ", int4=" + int4 + "]";
    }
}

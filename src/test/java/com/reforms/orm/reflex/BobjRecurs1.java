package com.reforms.orm.reflex;

public class BobjRecurs1 {

    private int t;

    private BobjRecurs2 br2;

    public BobjRecurs1(int t, BobjRecurs2 br2) {
        this.t = t;
        this.br2 = br2;
    }

    public int getT() {
        return t;
    }

    public BobjRecurs2 getBr2() {
        return br2;
    }
}

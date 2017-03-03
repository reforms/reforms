package com.reforms.orm.reflex;

public class BobjRecurs2 {

    private String str;

    private BobjRecurs1 br1;

    public BobjRecurs2(String str, BobjRecurs1 br1) {
        this.str = str;
        this.br1 = br1;
    }

    public String getStr() {
        return str;
    }

    public BobjRecurs1 getBr1() {
        return br1;
    }
}

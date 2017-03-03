package com.reforms.orm.reflex;

public class BobjStrange {

    private int index;

    private IBobj ibobj;

    public BobjStrange(int index, IBobj ibobj) {
        this.index = index;
        this.ibobj = ibobj;
    }

    public IBobj getIbobj() {
        return ibobj;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "[index=" + index + ", ibobj=" + ibobj + "]";
    }
}

package com.reforms.cf.struct;

/**
 * The Constant Pool Item
 *
 * @author evgenie
 */
public class CpItemStruct {

    private int tag;

    protected CpItemStruct(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

}

package com.reforms.cf.struct;

/**
 * The StackMapFrame struct
 *
 * @author evgenie
 */
public class StackMapFrameStruct {

    private final String name; // synthetic;
    private final int tag; // u1

    protected StackMapFrameStruct(String name, int tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public int getTag() {
        return tag;
    }

}

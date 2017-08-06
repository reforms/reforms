package com.reforms.cf.struct;

/**
 * The element value struct
 * @author evgenie
 *
 */
public class ElementValueStruct {

    private final String name; // synthetic
    private final char tag; // u1

    protected ElementValueStruct(String name, char tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public char getTag() {
        return tag;
    }

}

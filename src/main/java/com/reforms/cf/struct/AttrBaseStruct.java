package com.reforms.cf.struct;

/**
 * Attribute
 *
 * @author evgenie
 */
public class AttrBaseStruct {

    private String name; // syntatic
    private int nameIndex; // u2
    private int length; // u4

    protected AttrBaseStruct(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}

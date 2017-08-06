package com.reforms.cf.struct;

/**
 * The verification_type_info struct
 *
 * @author evgenie
 *
 */
public class VerificationTypeStruct {

    private final String name; // synthetic
    private final int tag; // u1

    protected VerificationTypeStruct(String name, int tag) {
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

package com.reforms.cf.struct;

/**
 * The Base TargetType Struct
 *
 * @author evgenie
 *
 */
public class TargetInfoStruct {

    private String name; // synthetic
    private int targetType; // u1

    protected TargetInfoStruct(String name, int targetType) {
        this.name = name;
        this.targetType = targetType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

}

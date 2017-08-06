package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The StackMapTable Attribute
 *
 * @author evgenie
 */
public class AttrStackMapTableStruct extends AttrBaseStruct {

    private int numberOfEntries; // u2
    private StackMapFrameStruct[] entries;

    public AttrStackMapTableStruct() {
        super(Attributes.STACK_MAP_TABLE_ATTR);
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(int numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public StackMapFrameStruct[] getEntries() {
        return entries;
    }

    public void setEntries(StackMapFrameStruct[] entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numberOfEntries=").append(numberOfEntries)
                .append(", entries=").append(Arrays.toString(entries));
        return builder.toString();
    }

}

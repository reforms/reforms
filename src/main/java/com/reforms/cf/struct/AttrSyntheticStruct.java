package com.reforms.cf.struct;

/**
 * The Synthetic Attribute
 *
 * @author evgenie
 */
public class AttrSyntheticStruct extends AttrBaseStruct {

    public AttrSyntheticStruct() {
        super(Attributes.SYNTHETIC_ATTR);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName());
        return builder.toString();
    }

}

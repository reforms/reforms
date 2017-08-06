package com.reforms.cf.struct;

/**
 * The Deprecated Attribute
 *
 * @author evgenie
 */
public class AttrDeprecatedStruct extends AttrBaseStruct {

    public AttrDeprecatedStruct() {
        super(Attributes.DEPRECATED_ATTR);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName());
        return builder.toString();
    }

}

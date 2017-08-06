package com.reforms.cf.struct;

/**
 * Field or Method
 *
 * @author evgenie
 */
public class MemberStruct {

    private StructType memberType; // synthetic
    private AccessFlags accessFlags; // u2
    private int nameIndex; // u2
    private int descriptorIndex; // u2
    private int attributesCount; // u2
    private Attributes attributes;

    public StructType getMemberType() {
        return memberType;
    }

    public void setMemberType(StructType memberType) {
        this.memberType = memberType;
    }

    public AccessFlags getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(AccessFlags accessFlags) {
        this.accessFlags = accessFlags;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public void setAttributesCount(int attributesCount) {
        this.attributesCount = attributesCount;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Member ").append(memberType);
        return builder.toString();
    }



}

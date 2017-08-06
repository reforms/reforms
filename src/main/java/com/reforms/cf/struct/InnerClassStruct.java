package com.reforms.cf.struct;

/**
 * The InnerClass struct
 *
 * @author evgenie
 */
public class InnerClassStruct {
    private StructType classType; // synthetic
    private int innerClassInfoIndex; // u2
    private int outerClassInfoIndex; // u2
    private int innerNameIndex; // u2
    private AccessFlags innerClassAccessFlags; // u2

    public StructType getClassType() {
        return classType;
    }

    public void setClassType(StructType classType) {
        this.classType = classType;
    }

    public int getInnerClassInfoIndex() {
        return innerClassInfoIndex;
    }

    public void setInnerClassInfoIndex(int innerClassInfoIndex) {
        this.innerClassInfoIndex = innerClassInfoIndex;
    }

    public int getOuterClassInfoIndex() {
        return outerClassInfoIndex;
    }

    public void setOuterClassInfoIndex(int outerClassInfoIndex) {
        this.outerClassInfoIndex = outerClassInfoIndex;
    }

    public int getInnerNameIndex() {
        return innerNameIndex;
    }

    public void setInnerNameIndex(int innerNameIndex) {
        this.innerNameIndex = innerNameIndex;
    }

    public AccessFlags getInnerClassAccessFlags() {
        return innerClassAccessFlags;
    }

    public void setInnerClassAccessFlags(AccessFlags innerClassAccessFlags) {
        this.innerClassAccessFlags = innerClassAccessFlags;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InnerClass innerClassInfoIndex=").append(innerClassInfoIndex).append(", outerClassInfoIndex=")
                .append(outerClassInfoIndex).append(", innerNameIndex=").append(innerNameIndex)
                .append(", innerClassAccessFlags=").append(innerClassAccessFlags);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

/**
 * The SourceFile Attribute
 *
 * @author evgenie
 */
public class AttrSourceFileStruct extends AttrBaseStruct {

    private int sourceFileIndex; // u2

    public AttrSourceFileStruct() {
        super(Attributes.SOURCE_FILE_ATTR);
    }

    public int getSourceFileIndex() {
        return sourceFileIndex;
    }

    public void setSourceFileIndex(int sourceFileIndex) {
        this.sourceFileIndex = sourceFileIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" sourceFileIndex=").append(sourceFileIndex);
        return builder.toString();
    }

}

package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The InnerClasses Attribute
 *
 * @author evgenie
 */
public class AttrInnerClassesStruct extends AttrBaseStruct {

    private int numberOfClasses; // u2
    private InnerClassStruct[] classes;

    public AttrInnerClassesStruct() {
        super(Attributes.INNER_CLASSES_ATTR);
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public InnerClassStruct[] getClasses() {
        return classes;
    }

    public void setClasses(InnerClassStruct[] classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numberOfClasses=").append(numberOfClasses)
                .append(", classes=").append(Arrays.toString(classes));
        return builder.toString();
    }

}

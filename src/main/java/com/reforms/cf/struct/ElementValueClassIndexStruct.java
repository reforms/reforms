package com.reforms.cf.struct;

/**
 * The element value ClassIndex struct
 *
 * @author evgenie
 *
 */
public class ElementValueClassIndexStruct extends ElementValueStruct {

    private int classIndex;

    public ElementValueClassIndexStruct() {
        super(ElementValues.CLASS_NAME_EV, ElementValues.CLASS_EV);
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementValue: ").append(getName()).append(" '").append(getTag()).append("' classIndex=")
                .append(classIndex);
        return builder.toString();
    }

}

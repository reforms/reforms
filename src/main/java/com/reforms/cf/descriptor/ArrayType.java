package com.reforms.cf.descriptor;

/**
 * The ArrayType: [ComponentType
 * @author evgenie
 */
public class ArrayType extends FieldDescriptor {

    private final int dimension;
    private final FieldDescriptor componentType;
    private final FieldDescriptor lowerType;

    public ArrayType(FieldDescriptor componentType, FieldDescriptor lowerType, int dimension) {
        super(Descriptors.ARRAY_TYPE, lowerType.getValue());
        this.componentType = componentType;
        this.dimension = dimension;
        this.lowerType = lowerType;
    }

    public FieldDescriptor getComponentType() {
        return componentType;
    }

    public FieldDescriptor getLowerType() {
        return lowerType;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public String getDescriptor() {
        StringBuilder report = new StringBuilder();
        for (int index = 0; index < dimension; index++) {
            report.append(getTerm());
        }
        report.append(lowerType.getDescriptor());
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ArrayType: ").append(getDescriptor());
        return builder.toString();
    }

}

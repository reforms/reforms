package com.reforms.cf.struct;

/**
 * The TypeAnnotation struct
 *
 * @author evgenie
 */
public class TypeAnnotationStruct {

    private TargetInfoStruct targetType;
    private TypePathStruct typePath;
    private ElementValueAnnotationStruct annotation;

    public TargetInfoStruct getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetInfoStruct targetType) {
        this.targetType = targetType;
    }

    public TypePathStruct getTypePath() {
        return typePath;
    }

    public void setTypePath(TypePathStruct typePath) {
        this.typePath = typePath;
    }

    public ElementValueAnnotationStruct getAnnotation() {
        return annotation;
    }

    public void setAnnotation(ElementValueAnnotationStruct annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypeAnnotation targetType=").append(targetType).append(", typePath=").append(typePath)
                .append(", annotation=").append(annotation);
        return builder.toString();
    }

}

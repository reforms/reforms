package com.reforms.cf.signature;

/**
 * The ArrayTypeSignature: [ JavaTypeSignature
 * @author evgenie
 */
public class ArrayTypeSignature implements Signature {

    /** BaseType Or ReferenceTypeSignature */
    private final Signature javaType;

    public ArrayTypeSignature(Signature javaType) {
        this.javaType = javaType;
    }

    public Signature getJavaType() {
        return javaType;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        report.append("[").append(javaType.getSignature());
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ArrayTypeSignature ").append(getSignature());
        return builder.toString();
    }

}

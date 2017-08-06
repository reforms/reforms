package com.reforms.cf.signature;

/**
 * The ThrowsSignature: ^ ClassTypeSignature OR ^ TypeVariableSignature
 * @author evgenie
 */
public class ThrowsSignature implements Signature {

    /** ClassTypeSignature Or TypeVariableSignature */
    private final Signature referenceType;

    public ThrowsSignature(Signature referenceType) {
        this.referenceType = referenceType;
    }

    public Signature getReferenceType() {
        return referenceType;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        report.append("^").append(referenceType.getSignature());
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ThrowsSignature ").append(getSignature());
        return builder.toString();
    }

}

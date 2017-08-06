package com.reforms.cf.signature;

/**
 * The TypeArgument: [WildcardIndicator] ReferenceTypeSignature *
 * @author evgenie
 */
public class TypeArgument {
    private final char wildcardIndicator;
    private final Signature signature;

    public TypeArgument(char wildcardIndicator, Signature signature) {
        this.wildcardIndicator = wildcardIndicator;
        this.signature = signature;
    }

    public char getWildcardIndicator() {
        return wildcardIndicator;
    }

    public Signature getSignature() {
        return signature;
    }

    public boolean hasSignature() {
        return signature != null;
    }

    public String form() {
        StringBuilder report = new StringBuilder();
        if (Signatures.ABSENT_WILDCARD_INDICATOR != wildcardIndicator) {
            report.append(wildcardIndicator);
        }
        if (signature != null) {
            report.append(signature.getSignature());
        }
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypeArgument ").append(form());
        return builder.toString();
    }

}

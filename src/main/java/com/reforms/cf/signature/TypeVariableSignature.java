package com.reforms.cf.signature;

/**
 * The TypeVariableSignature: T Identifier ;
 * @author evgenie
 */
public class TypeVariableSignature implements Signature {
    private final String identifier;

    public TypeVariableSignature(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        report.append("T").append(identifier).append(";");
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypeVariableSignature ").append(getSignature());
        return builder.toString();
    }

}

package com.reforms.cf.signature;


/**
 * The SimpleClassTypeSignature: Identifier [TypeArguments]
 * @author evgenie
 */
public class SimpleClassTypeSignature implements Signature {

    private final String identifier;
    private final String shortIdenitifier; // synthetic
    private final TypeArgument[] typeArguments;

    public SimpleClassTypeSignature(String identifier, String shortIdenitifier, TypeArgument[] typeArguments) {
        this.identifier = identifier;
        this.shortIdenitifier = shortIdenitifier;
        this.typeArguments = typeArguments;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getShortIdentifier() {
        return shortIdenitifier;
    }

    public TypeArgument[] getTypeArguments() {
        return typeArguments;
    }

    public boolean hasTypeArguments() {
        return typeArguments != null && typeArguments.length > 0;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        report.append(identifier);
        if (typeArguments != null && typeArguments.length > 0) {
            report.append("<");
            for (TypeArgument typeArgument : typeArguments) {
                report.append(typeArgument.form());
            }
            report.append(">");
        }
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleClassTypeSignature: ").append(getSignature());
        return builder.toString();
    }

}

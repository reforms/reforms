package com.reforms.cf.signature;

/**
 * The TypeParameter: Identifier ClassBound {InterfaceBound}
 * @author evgenie
 */
public class TypeParameter {
    private final String identifier;
    private final Signature classBound;
    private final Signature[] interfacesBound;

    public TypeParameter(String identifier, Signature classBound, Signature[] interfacesBound) {
        this.identifier = identifier;
        this.classBound = classBound;
        this.interfacesBound = interfacesBound;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Signature getClassBound() {
        return classBound;
    }

    public boolean hasClassBound() {
        return classBound != null;
    }

    public Signature[] getInterfacesBound() {
        return interfacesBound;
    }

    public boolean hasInterfacesBound() {
        return interfacesBound != null && interfacesBound.length > 0;
    }

    public String form() {
        StringBuilder report = new StringBuilder();
        report.append(identifier).append(":");
        if (classBound != null) {
            report.append(classBound.getSignature());
        }
        if (interfacesBound != null && interfacesBound.length > 0) {
            for (Signature interfaceBound : interfacesBound) {
                report.append(":");
                report.append(interfaceBound.getSignature());

            }
        }
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypeParameter: ").append(form());
        return builder.toString();
    }

}

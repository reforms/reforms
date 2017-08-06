package com.reforms.cf.signature;

/**
 * The Method Signature: [TypeParameters] ( {JavaTypeSignature} ) Result {ThrowsSignature}
 * @author evgenie
 */
public class MethodSignature implements Signature {

    private final TypeParameter[] typeParameters;
    /** BaseType Or ReferenceTypeSignature */
    private final Signature[] javaTypes;
    /** BaseType Or ReferenceTypeSignature or VoidType */
    private final Signature result;
    private final ThrowsSignature[] throwsSignatures;

    public MethodSignature(TypeParameter[] typeParameters, Signature[] javaTypes, Signature result,
            ThrowsSignature[] throwsSignatures) {
        this.typeParameters = typeParameters;
        this.javaTypes = javaTypes;
        this.result = result;
        this.throwsSignatures = throwsSignatures;
    }

    public TypeParameter[] getTypeParameters() {
        return typeParameters;
    }

    public boolean hasTypeParameters() {
        return typeParameters != null && typeParameters.length > 0;
    }

    public Signature[] getJavaTypes() {
        return javaTypes;
    }

    public Signature getResult() {
        return result;
    }

    public ThrowsSignature[] getThrowsSignatures() {
        return throwsSignatures;
    }

    public boolean hasThrowsSignatures() {
        return throwsSignatures != null && throwsSignatures.length > 0;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        if (typeParameters != null && typeParameters.length > 0) {
            report.append("<");
            for (TypeParameter typeParameter : typeParameters) {
                report.append(typeParameter.form());
            }
            report.append(">");
        }
        report.append("(");
        if (javaTypes != null && javaTypes.length > 0) {
            for (Signature javaType : javaTypes) {
                report.append(javaType.getSignature());
            }
        }
        report.append(")");
        report.append(result.getSignature());
        if (throwsSignatures != null && throwsSignatures.length > 0) {
            for (ThrowsSignature throwsSignature : throwsSignatures) {
                report.append(throwsSignature.getSignature());
            }
        }

        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MethodSignature: ").append(getSignature());
        return builder.toString();
    }

}

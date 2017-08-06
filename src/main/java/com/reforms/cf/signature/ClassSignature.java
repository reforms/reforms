package com.reforms.cf.signature;

/**
 * The Class Signature
 * @author evgenie
 */
public class ClassSignature implements Signature {

    private final TypeParameter[] typeParameters;
    private final ClassTypeSignature superclass;
    private final ClassTypeSignature[] superInterfaces;

    public ClassSignature(TypeParameter[] typeParameters, ClassTypeSignature superclass,
            ClassTypeSignature[] superInterfaces) {
        this.typeParameters = typeParameters;
        this.superclass = superclass;
        this.superInterfaces = superInterfaces;
    }

    public TypeParameter[] getTypeParameters() {
        return typeParameters;
    }

    public boolean hasTypeParameters() {
        return typeParameters != null && typeParameters.length > 0;
    }

    public ClassTypeSignature getSuperclass() {
        return superclass;
    }

    public boolean hasSuperclass() {
        return superclass != null;
    }

    public ClassTypeSignature[] getSuperInterfaces() {
        return superInterfaces;
    }

    public boolean hasSuperInterfaces() {
        return superInterfaces != null && superInterfaces.length > 0;
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
        report.append(superclass.getSignature());
        if (superInterfaces != null && superInterfaces.length > 0) {
            for (ClassTypeSignature superInterface : superInterfaces) {
                report.append(superInterface.getSignature());
            }
        }
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassSignature ").append(getSignature());
        return builder.toString();
    }

}

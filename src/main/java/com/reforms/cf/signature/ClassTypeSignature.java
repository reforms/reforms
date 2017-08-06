package com.reforms.cf.signature;

/**
 * The Class Type Signature: L[PackageSpecifier] SimpleClassTypeSignature {ClassTypeSignatureSuffix};
 * @author evgenie
 */
public class ClassTypeSignature implements Signature {

    private final String packageSpecifier;
    private final SimpleClassTypeSignature simpleClassType;
    private final SimpleClassTypeSignature[] suffixes;

    public ClassTypeSignature(String packageSpecifier, SimpleClassTypeSignature simpleClassType,
            SimpleClassTypeSignature[] suffixes) {
        this.packageSpecifier = packageSpecifier;
        this.simpleClassType = simpleClassType;
        this.suffixes = suffixes;
    }

    public String getPackageSpecifier() {
        return packageSpecifier;
    }

    public SimpleClassTypeSignature getSimpleClassType() {
        return simpleClassType;
    }

    public SimpleClassTypeSignature[] getSuffixes() {
        return suffixes;
    }

    public boolean hasSuffixes() {
        return suffixes != null && suffixes.length > 0;
    }

    @Override
    public String getSignature() {
        StringBuilder report = new StringBuilder();
        report.append("L");
        if (packageSpecifier != null) {
            report.append(packageSpecifier);
        }
        report.append(simpleClassType.getSignature());
        if (suffixes != null && suffixes.length > 0) {
            for (SimpleClassTypeSignature suffix : suffixes) {
                report.append(".");
                report.append(suffix.getSignature());
            }
        }
        report.append(";");
        return report.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClassTypeSignature: ").append(getSignature());
        return builder.toString();
    }

}

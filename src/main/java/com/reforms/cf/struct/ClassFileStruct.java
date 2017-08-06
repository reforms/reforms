package com.reforms.cf.struct;

/**
 * The ClassFile Structure
 * @author evgenie
 */
public class ClassFileStruct {
    private StructType classType; // synthetic;
    private int magic; // u4
    private int minorVersion; // u2
    private int majorVersion; // u2
    private int constantPoolCount; // u2
    private ConstantPool constantPool;
    private AccessFlags accessFlags; // u2
    private int thisClass; // u2
    private int superClass; // u2
    private int interfacesCount; // u2
    private int[] interfaces; // u2Array
    private int fieldsCount; // u2
    private MemberStruct fields[];
    private int methodsCount; // u2
    private MemberStruct[] methods;
    private int attributesCount; // u2
    private Attributes attributes;

    public StructType getClassType() {
        return classType;
    }

    public void setClassType(StructType classType) {
        this.classType = classType;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getConstantPoolCount() {
        return constantPoolCount;
    }

    public void setConstantPoolCount(int constantPoolCount) {
        this.constantPoolCount = constantPoolCount;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public AccessFlags getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(AccessFlags accessFlags) {
        this.accessFlags = accessFlags;
    }

    public int getThisClass() {
        return thisClass;
    }

    public void setThisClass(int thisClass) {
        this.thisClass = thisClass;
    }

    public int getSuperClass() {
        return superClass;
    }

    public boolean hasSuperClass() {
        return superClass != 0;
    }

    public void setSuperClass(int superClass) {
        this.superClass = superClass;
    }

    public int getInterfacesCount() {
        return interfacesCount;
    }

    public void setInterfacesCount(int interfacesCount) {
        this.interfacesCount = interfacesCount;
    }

    public boolean hasInterfaces() {
        return interfacesCount > 0 && interfaces != null && interfaces.length > 0;
    }

    public int[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(int[] interfaces) {
        this.interfaces = interfaces;
    }

    public int getFieldsCount() {
        return fieldsCount;
    }

    public void setFieldsCount(int fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    public MemberStruct[] getFields() {
        return fields;
    }

    public boolean hasFields() {
        return fieldsCount > 0 && fields != null && fields.length > 0;
    }

    public void setFields(MemberStruct[] fields) {
        this.fields = fields;
    }

    public int getMethodsCount() {
        return methodsCount;
    }

    public void setMethodsCount(int methodsCount) {
        this.methodsCount = methodsCount;
    }

    public MemberStruct[] getMethods() {
        return methods;
    }

    public void setMethods(MemberStruct[] methods) {
        this.methods = methods;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public void setAttributesCount(int attributesCount) {
        this.attributesCount = attributesCount;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

}

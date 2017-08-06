package com.reforms.cf.struct;

/**
 * The enum for AccessFlags
 * @author evgenie
 */
public enum AccessFlag {

    PUBLIC_FLAG(0x0001, "public"),
    PRIVATE_FLAG(0x0002, "private"),
    PROTECTED_FLAG(0x0004, "protected"),
    STATIC_FLAG(0x0008, "static"),
    FINAL_FLAG(0x0010, "final"),
    SUPER_FLAG(0x0020, "super"),
    SYNCHRONIZED_FLAG(0x0020, "synchronized"),
    VOLATILE_FLAG(0x0040, "volatile"),
    BRIDGE_FLAG(0x0040, "bridge"),
    TRANSIENT_FLAG(0x0080, "transient"),
    VARARGS_FLAG(0x0080, "varargs"),
    NATIVE_FLAG(0x0100, "native"),
    INTERFACE_FLAG(0x0200, "interface"),
    ABSTRACT_FLAG(0x0400, "abstract"),
    STRICT_FLAG(0x0800, "strictfp"),
    SYNTHETIC_FLAG(0x1000, "synthetic"),
    ANNOTATION_FLAG(0x2000, "@interface"),
    ENUM_FLAG(0x4000, "enum"),
    MANDATED_FLAG(0x8000, "mandated");

    private int code;
    private String name;

    private AccessFlag(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public boolean matches(int accessFlags) {
        return (accessFlags & code) != 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccessFlag [code=").append(code).append(", name=").append(name).append("]");
        return builder.toString();
    }

}

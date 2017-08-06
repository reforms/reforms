package com.reforms.cf.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.reforms.cf.struct.AccessFlag.*;

/**
 * Help for AccessFlag api
 * @author evgenie
 */
public class AccessFlags {

    public static AccessFlags EMPTY_FLAFS = new AccessFlags(null, 0, Collections.<AccessFlag>emptyList());

    private static final List<AccessFlag> CLASS_FLAGS = Arrays.asList(PUBLIC_FLAG, FINAL_FLAG, SUPER_FLAG,
            INTERFACE_FLAG, ABSTRACT_FLAG, SYNTHETIC_FLAG, ANNOTATION_FLAG, ENUM_FLAG);

    private static final List<AccessFlag> METHOD_FLAGS = Arrays.asList(PUBLIC_FLAG, PRIVATE_FLAG, PROTECTED_FLAG,
            STATIC_FLAG, FINAL_FLAG, SYNCHRONIZED_FLAG, BRIDGE_FLAG, VARARGS_FLAG, NATIVE_FLAG, ABSTRACT_FLAG,
            STRICT_FLAG, SYNTHETIC_FLAG);

    private static final List<AccessFlag> FIELD_FLAGS = Arrays.asList(PUBLIC_FLAG, PRIVATE_FLAG, PROTECTED_FLAG,
            STATIC_FLAG, FINAL_FLAG, VOLATILE_FLAG, TRANSIENT_FLAG, SYNTHETIC_FLAG, ENUM_FLAG);

    private static final List<AccessFlag> PARAM_FLAGS = Arrays.asList(FINAL_FLAG, SYNTHETIC_FLAG, MANDATED_FLAG);

    private static final List<AccessFlag> INNER_CLASS_FLAGS = Arrays.asList(PUBLIC_FLAG, PRIVATE_FLAG, PROTECTED_FLAG,
            STATIC_FLAG, FINAL_FLAG, INTERFACE_FLAG, ABSTRACT_FLAG, SYNTHETIC_FLAG, ANNOTATION_FLAG, ENUM_FLAG);

    public static AccessFlags build(int accessFlags, StructType ownerType) {
        List<AccessFlag> accessFlagList = Collections.emptyList();
        if (ownerType.isBaseClass()) {
            accessFlagList = CLASS_FLAGS;
        }
        if (ownerType.isNestedClass()) {
            if (ownerType.isNestedStaticClass()) {
                accessFlags |= STATIC_FLAG.getCode();
            }
            accessFlagList = INNER_CLASS_FLAGS;
        }
        if (ownerType.isField()) {
            accessFlagList = FIELD_FLAGS;
        }
        if (ownerType.isMethod()) {
            accessFlagList = METHOD_FLAGS;
        }
        if (ownerType.isParam()) {
            accessFlagList = PARAM_FLAGS;
        }

        return new AccessFlags(ownerType, accessFlags, accessFlagList);
    }

    private final StructType ownerType;
    private final int accessFlags;
    private final List<AccessFlag> ownerFlags;

    private AccessFlags(StructType ownerType, int accessFlags, List<AccessFlag> ownerFlags) {
        this.ownerType = ownerType;
        this.accessFlags = accessFlags;
        this.ownerFlags = ownerFlags;
    }

    public StructType getOwnerType() {
        return ownerType;
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public boolean hasPublicFlag() {
        return PUBLIC_FLAG.matches(accessFlags) && ownerFlags.contains(PUBLIC_FLAG);
    }

    public boolean hasPrivateFlag() {
        return PRIVATE_FLAG.matches(accessFlags) && ownerFlags.contains(PRIVATE_FLAG);
    }

    public boolean hasProtectedFlag() {
        return PROTECTED_FLAG.matches(accessFlags) && ownerFlags.contains(PROTECTED_FLAG);
    }

    public boolean hasStaticFlag() {
        return STATIC_FLAG.matches(accessFlags) && ownerFlags.contains(STATIC_FLAG);
    }

    public boolean hasFinalFlag() {
        return FINAL_FLAG.matches(accessFlags) && ownerFlags.contains(FINAL_FLAG);
    }

    public boolean hasSuperFlag() {
        return SUPER_FLAG.matches(accessFlags) && ownerFlags.contains(SUPER_FLAG);
    }

    public boolean hasSynchronizedFlag() {
        return SYNCHRONIZED_FLAG.matches(accessFlags) && ownerFlags.contains(SYNCHRONIZED_FLAG);
    }

    public boolean hasVolatileFlag() {
        return VOLATILE_FLAG.matches(accessFlags) && ownerFlags.contains(VOLATILE_FLAG);
    }

    public boolean hasBridgeFlag() {
        return BRIDGE_FLAG.matches(accessFlags) && ownerFlags.contains(BRIDGE_FLAG);
    }

    public boolean hasTransientFlag() {
        return TRANSIENT_FLAG.matches(accessFlags) && ownerFlags.contains(TRANSIENT_FLAG);
    }

    public boolean hasVarargsFlag() {
        return VARARGS_FLAG.matches(accessFlags) && ownerFlags.contains(VARARGS_FLAG);
    }

    public boolean hasNativeFlag() {
        return NATIVE_FLAG.matches(accessFlags) && ownerFlags.contains(NATIVE_FLAG);
    }

    public boolean hasInterfaceFlag() {
        return INTERFACE_FLAG.matches(accessFlags) && ownerFlags.contains(INTERFACE_FLAG);
    }

    public boolean hasAbstractFlag() {
        return ABSTRACT_FLAG.matches(accessFlags) && ownerFlags.contains(ABSTRACT_FLAG);
    }

    public boolean hasStrictfpFlag() {
        return STRICT_FLAG.matches(accessFlags) && ownerFlags.contains(STRICT_FLAG);
    }

    public boolean hasSyntheticFlag() {
        return SYNTHETIC_FLAG.matches(accessFlags) && ownerFlags.contains(SYNTHETIC_FLAG);
    }

    public boolean hasAnnotationFlag() {
        return ANNOTATION_FLAG.matches(accessFlags) && ownerFlags.contains(ANNOTATION_FLAG);
    }

    public boolean hasEnumFlag() {
        return ENUM_FLAG.matches(accessFlags) && ownerFlags.contains(ENUM_FLAG);
    }

    public boolean isClass() {
        return ownerType.isClass() && !hasInterfaceFlag() && !hasEnumFlag() && !hasAnnotationFlag();
    }

    public int getPrior() {
        if (hasPublicFlag()) {
            return 4;
        }
        if (hasProtectedFlag()) {
            return 3;
        }
        if (!hasPrivateFlag()) {
            return 2;
        }
        return 1;
    }

    public List<AccessFlag> getVisibleFlags() {
        List<AccessFlag> flags = new ArrayList<>();
        if (hasPublicFlag()) {
            flags.add(PUBLIC_FLAG);
        }
        if (hasPrivateFlag()) {
            flags.add(PRIVATE_FLAG);
        }
        if (hasProtectedFlag()) {
            flags.add(PROTECTED_FLAG);
        }
        if (hasStaticFlag()) {
            flags.add(STATIC_FLAG);
        }
        if (hasFinalFlag()) {
            flags.add(FINAL_FLAG);
        }
        if (hasSynchronizedFlag()) {
            flags.add(SYNCHRONIZED_FLAG);
        }
        if (hasVolatileFlag()) {
            flags.add(VOLATILE_FLAG);
        }
        if (hasTransientFlag()) {
            flags.add(TRANSIENT_FLAG);
        }
        if (hasNativeFlag()) {
            flags.add(NATIVE_FLAG);
        }
        if (hasAbstractFlag()) {
            flags.add(ABSTRACT_FLAG);
        }
        if (hasStrictfpFlag()) {
            flags.add(STRICT_FLAG);
        }
        if (hasInterfaceFlag()) {
            flags.add(INTERFACE_FLAG);
        }
        if (hasAnnotationFlag()) {
            flags.add(ANNOTATION_FLAG);
        }
        if (hasEnumFlag()) {
            flags.add(ENUM_FLAG);
        }
        return flags;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccessFlags: ");
        builder.append(ownerType.name());
        for (AccessFlag accessFlag : ownerFlags) {
            if ((accessFlags & accessFlag.getCode()) != 0){
                builder.append(" ");
                builder.append(accessFlag.getName());
            }
        }
        return builder.toString();
    }

}

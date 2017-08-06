package com.reforms.cf;

import com.reforms.cf.struct.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static com.reforms.cf.struct.StructType.CLASS_NESTED_ANONYMOUS;
import static com.reforms.cf.struct.StructType.CLASS_NESTED_LOCAL;

/**
 * The parser for ClassFile struct
 * @author evgenie
 *
 */
public class ClassFileParser {

    private String sourceName;
    private DataStream stream = null;
    private ClassFileStruct classFile = null;

    public ClassFileStruct parse(String sourceName, DataStream stream) {
        this.sourceName = sourceName;
        this.stream = stream;
        return parseClassFile();
    }

    private ClassFileStruct parseClassFile() {
        StructType classType = StructType.CLASS_BASE;
        classFile = new ClassFileStruct();
        classFile.setClassType(classType);
        classFile.setMagic(stream.u4());
        classFile.setMinorVersion(stream.u2());
        classFile.setMajorVersion(stream.u2());
        classFile.setConstantPoolCount(stream.u2());
        classFile.setConstantPool(parseConstantPool(classFile.getConstantPoolCount()));
        classFile.setAccessFlags(AccessFlags.build(stream.u2(), classType));
        classFile.setThisClass(stream.u2());
        classFile.setSuperClass(stream.u2());
        classFile.setInterfacesCount(stream.u2());
        classFile.setInterfaces(parseInterfaces(classFile.getInterfacesCount()));
        classFile.setFieldsCount(stream.u2());
        classFile.setFields(parseFields(classFile.getFieldsCount()));
        classFile.setMethodsCount(stream.u2());
        classFile.setMethods(parseMethods(classFile.getMethodsCount()));
        classFile.setAttributesCount(stream.u2());
        classFile.setAttributes(parseAttributes(classFile.getAttributesCount(), classType));
        return classFile;
    }

    private ConstantPool parseConstantPool(int count) {
        CpItemStruct[] cpItems = new CpItemStruct[count];
        for (int index = 1; index < count; index++) {
            int tag = stream.u1();
            cpItems[index] = parseConstantPoolItem(tag);
            if (ConstantPool.LONG_TAG == tag || ConstantPool.DOUBLE_TAG == tag) {
                index++;
            }
        }
        return new ConstantPool(cpItems);
    }

    private CpItemStruct parseConstantPoolItem(int tag) {
        if (ConstantPool.UTF8_TAG == tag) {
            return parseCpUtf8Item();
        }
        if (ConstantPool.INTEGER_TAG == tag) {
            return parseCpIntegerItem();
        }
        if (ConstantPool.FLOAT_TAG == tag) {
            return parseCpFloatItem();
        }
        if (ConstantPool.LONG_TAG == tag) {
            return parseCpLongItem();
        }
        if (ConstantPool.DOUBLE_TAG == tag) {
            return parseCpDoubleItem();
        }
        if (ConstantPool.CLASS_TAG == tag) {
            return parseCpClassItem();
        }
        if (ConstantPool.STRING_TAG == tag) {
            return parseCpStringItem();
        }
        if (ConstantPool.FIELD_REF_TAG == tag
                || ConstantPool.METHOD_REF_TAG == tag
                || ConstantPool.INTERFACE_METHOD_REF_TAG == tag) {
            return parseCpMemberRefItem(tag);
        }
        if (ConstantPool.NAME_AND_TYPE_TAG == tag) {
            return parseCpNameAndTypeItem();
        }
        if (ConstantPool.METHOD_HANDLE_TAG == tag) {
            return parseCpMethodHandleItem();
        }
        if (ConstantPool.METHOD_TYPE_TAG == tag) {
            return parseCpMethodTypeItem();
        }
        if (ConstantPool.INVOKE_DYNAMIC_TAG == tag) {
            return parseCpInvokeDynamicItem();
        }

        throw new ExceptionBuilder()
            .area(ClassFormatException.PARSE_FILE_AREA)
            .reason("Неизвестный tag \"{0}\" в ConstantPool", tag)
            .effect("Невозможно разобрать класс \"{0}\"", sourceName)
            .action("Необходимо доработать парсер класса секция ConstantPool или проверить валидность формата класса \"{0}\"", sourceName)
            .exception();
    }

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF8");

    private CpUtf8Struct parseCpUtf8Item() {
        CpUtf8Struct cpUtf8 = new CpUtf8Struct();
        cpUtf8.setLength(stream.u2());
        cpUtf8.setValue(new String(stream.u1Array(cpUtf8.getLength()), CHARSET_UTF8));
        return cpUtf8;
    }

    private CpIntegerStruct parseCpIntegerItem() {
        CpIntegerStruct cpInteger = new CpIntegerStruct();
        cpInteger.setValue(ByteBuffer.wrap(stream.u1Array(4)).getInt());
        return cpInteger;
    }

    private CpFloatStruct parseCpFloatItem() {
        CpFloatStruct cpFloat = new CpFloatStruct();
        cpFloat.setValue(ByteBuffer.wrap(stream.u1Array(4)).getFloat());
        return cpFloat;
    }

    private CpLongStruct parseCpLongItem() {
        CpLongStruct cpLong = new CpLongStruct();
        cpLong.setValue(ByteBuffer.wrap(stream.u1Array(8)).getLong());
        return cpLong;
    }

    private CpDoubleStruct parseCpDoubleItem() {
        CpDoubleStruct cpDouble = new CpDoubleStruct();
        cpDouble.setValue(ByteBuffer.wrap(stream.u1Array(8)).getDouble());
        return cpDouble;
    }

    private CpClassStruct parseCpClassItem() {
        CpClassStruct cpClass = new CpClassStruct();
        cpClass.setNameIndex(stream.u2());
        return cpClass;
    }

    private CpStringStruct parseCpStringItem() {
        CpStringStruct cpString = new CpStringStruct();
        cpString.setStringIndex(stream.u2());
        return cpString;
    }

    private CpMemberRefStruct parseCpMemberRefItem(int tag) {
        CpMemberRefStruct cpMemberRef = new CpMemberRefStruct(tag);
        cpMemberRef.setClassIndex(stream.u2());
        cpMemberRef.setNameAndTypeIndex(stream.u2());
        return cpMemberRef;
    }

    private CpNameAndTypeStruct parseCpNameAndTypeItem() {
        CpNameAndTypeStruct cpNameAndType = new CpNameAndTypeStruct();
        cpNameAndType.setNameIndex(stream.u2());
        cpNameAndType.setDescriptorIndex(stream.u2());
        return cpNameAndType;
    }

    private CpMethodHandleStruct parseCpMethodHandleItem() {
        CpMethodHandleStruct cpMethodHandle = new CpMethodHandleStruct();
        cpMethodHandle.setReferenceKind(stream.u1());
        cpMethodHandle.setReferenceIndex(stream.u2());
        return cpMethodHandle;
    }

    private CpMethodTypeStruct parseCpMethodTypeItem() {
        CpMethodTypeStruct cpMethodType = new CpMethodTypeStruct();
        cpMethodType.setDescriptorIndex(stream.u2());
        return cpMethodType;
    }

    private CpInvokeDynamicStruct parseCpInvokeDynamicItem() {
        CpInvokeDynamicStruct cpInvokeDynamic = new CpInvokeDynamicStruct();
        cpInvokeDynamic.setBootstrapMethodAttrIndex(stream.u2());
        cpInvokeDynamic.setNameAndTypeIndex(stream.u2());
        return cpInvokeDynamic;
    }
    private int[] parseInterfaces(int count) {
        return stream.u2Array(count);
    }

    private MemberStruct[] parseFields(int count) {
        return parseMembers(count, true);
    }

    private MemberStruct[] parseMethods(int count) {
        return parseMembers(count, false);
    }

    private MemberStruct[] parseMembers(int count, boolean field) {
        StructType ownerType = field ? StructType.FIELD_BASE : StructType.METHOD_BASE;
        MemberStruct[] members = new MemberStruct[count];
        for (int index = 0; index < count; index++) {
            members[index] = parseMember(ownerType);
        }
        return members;
    }

    private MemberStruct parseMember(StructType ownerType) {
        MemberStruct member = new MemberStruct();
        member.setMemberType(ownerType);
        member.setAccessFlags(AccessFlags.build(stream.u2(), ownerType));
        member.setNameIndex(stream.u2());
        member.setDescriptorIndex(stream.u2());
        member.setAttributesCount(stream.u2());
        member.setAttributes(parseAttributes(member.getAttributesCount(), ownerType));
        return member;
    }

    private Attributes parseAttributes(int count, StructType owner) {
        AttrBaseStruct[] attributes = new AttrBaseStruct[count];
        for (int index = 0; index < count; index++) {
            attributes[index] = parseAttribute();
        }
        return new Attributes(owner, attributes);
    }

    private AttrBaseStruct parseAttribute() {
        int nameIndex = stream.u2();
        int length = stream.u4();
        ConstantPool constantPool = classFile.getConstantPool();
        String attrName = constantPool.getStringValue(nameIndex);
        AttrBaseStruct attribute = parseAttribute(attrName, length);
        attribute.setNameIndex(nameIndex);
        attribute.setLength(length);
        return attribute;
    }

    private AttrBaseStruct parseAttribute(String attrName, int length) {
        if (Attributes.isConstantValue(attrName)) {
            return parseConstantValueAttribute();
        }
        if (Attributes.isCode(attrName)) {
            return parseCodeAttribute();
        }
        if (Attributes.isStackMapTable(attrName)) {
            return parseStackMapTableAttribute();
        }
        if (Attributes.isExceptions(attrName)) {
            return parseExceptionsAttribute();
        }
        if (Attributes.isInnerClasses(attrName)) {
            return parseInnerClassesAttribute();
        }
        if (Attributes.isEnclosingMethod(attrName)) {
            return parseEnclosingMethodAttribute();
        }
        if (Attributes.isSynthetic(attrName)) {
            return parseSyntheticAttribute();
        }
        if (Attributes.isSignature(attrName)) {
            return parseSignatureAttribute();
        }
        if (Attributes.isSourceFile(attrName)) {
            return parseSourceFileAttribute();
        }
        if (Attributes.isSourceDebugExtension(attrName)) {
            return parseSourceDebugExtensionAttribute(length);
        }
        if (Attributes.isLineNumberTable(attrName)) {
            return parseLineNumberTableAttribute();
        }
        if (Attributes.isLocalVariableTable(attrName)) {
            return parseLocalVariableTableAttribute();
        }
        if (Attributes.isLocalVariableTypeTable(attrName)) {
            return parseLocalVariableTypeTableAttribute();
        }
        if (Attributes.isDeprecated(attrName)) {
            return parseDeprecatedAttribute();
        }
        if (Attributes.isRuntimeVisibleAnnotations(attrName)) {
            return parseRuntimeVisibleAnnotationsAttribute();
        }
        if (Attributes.isRuntimeInvisibleAnnotations(attrName)) {
            return parseRuntimeInvisibleAnnotationsAttribute();
        }
        if (Attributes.isRuntimeVisibleParameterAnnotations(attrName)) {
            return parseRuntimeVisibleParameterAnnotationsAttribute();
        }
        if (Attributes.isRuntimeInvisibleParameterAnnotations(attrName)) {
            return parseRuntimeInvisibleParameterAnnotationsAttribute();
        }
        if (Attributes.isRuntimeVisibleTypeAnnotations(attrName)) {
            return parseRuntimeVisibleTypeAnnotationsAttribute();
        }
        if (Attributes.isRuntimeInvisibleTypeAnnotations(attrName)) {
            return parseRuntimeInvisibleTypeAnnotationsAttribute();
        }
        if (Attributes.isAnnotationDefault(attrName)) {
            return parseAnnotationDefaultAttribute();
        }
        if (Attributes.isMethodParameters(attrName)) {
            return parseMethodParametersAttribute();
        }
        if (Attributes.isBootstrapMethods(attrName)) {
            return parseBootstrapMethodsAttribute();
        }
        return parseUnknownAttribute(length);
    }

    private AttrConstantValueStruct parseConstantValueAttribute() {
        AttrConstantValueStruct attrConstantValue = new AttrConstantValueStruct();
        attrConstantValue.setConstantValueIndex(stream.u2());
        return attrConstantValue;
    }

    private AttrCodeStruct parseCodeAttribute() {
        AttrCodeStruct attrCode = new AttrCodeStruct();
        attrCode.setMaxStack(stream.u2());
        attrCode.setMaxLocals(stream.u2());
        attrCode.setCodeLength(stream.u4());
        attrCode.setCode(stream.u1Array(attrCode.getCodeLength()));
        attrCode.setExceptionTableLength(stream.u2());
        attrCode.setExceptionTable(parseExceptionTables(attrCode.getExceptionTableLength()));
        attrCode.setAttributesCount(stream.u2());
        attrCode.setAttributes(parseAttributes(attrCode.getAttributesCount(), StructType.ATTRIBUTE));
        return attrCode;
    }

    private ExceptionRecordStruct[] parseExceptionTables(int count) {
        ExceptionRecordStruct[] exceptions = new ExceptionRecordStruct[count];
        for (int index = 0; index < count; index++) {
            exceptions[index] = parseExceptionTable();
        }
        return exceptions;
    }

    private ExceptionRecordStruct parseExceptionTable() {
        ExceptionRecordStruct exception = new ExceptionRecordStruct();
        exception.setStartPc(stream.u2());
        exception.setEndPc(stream.u2());
        exception.setHandlerPc(stream.u2());
        exception.setCatchType(stream.u2());
        return exception;
    }

    private AttrStackMapTableStruct parseStackMapTableAttribute() {
        AttrStackMapTableStruct attrStackMapTable = new AttrStackMapTableStruct();
        attrStackMapTable.setNumberOfEntries(stream.u2());
        attrStackMapTable.setEntries(parseStackMapFrames(attrStackMapTable.getNumberOfEntries()));
        return attrStackMapTable;
    }

    private StackMapFrameStruct[] parseStackMapFrames(int count) {
        StackMapFrameStruct[] stackMapFrames = new StackMapFrameStruct[count];
        for (int index = 0; index < count; index++) {
            int tag = stream.u1();
            stackMapFrames[index] = parseStackMapFrame(tag);
        }
        return stackMapFrames;
    }

    private StackMapFrameStruct parseStackMapFrame(int tag) {
        if (StackMapFrames.isSameFrame(tag)) {
            return new StackMapFrameSameStruct(tag);
        }
        if (StackMapFrames.isSameLocalsStackItemFrame(tag)) {
            return parseSameLocalsStackItemFrame(tag);
        }
        if (StackMapFrames.isSameLocalsStackItemFrameExtended(tag)) {
            return parseSameLocalsStackItemFrameExtended();
        }
        if (StackMapFrames.isChopFrame(tag)) {
            return parseChopFrame(tag);
        }
        if (StackMapFrames.isSameFrameExtended(tag)) {
            return parseSameFrameExtended();
        }
        if (StackMapFrames.isAppendFrame(tag)) {
            return parseAppendFrame(tag);
        }
        if (StackMapFrames.isFullFrame(tag)) {
            return parseFullFrame();
        }
        throw new ExceptionBuilder()
            .area(ClassFormatException.PARSE_FILE_AREA)
            .reason("Неизвестный tag \"{0}\" в StackMapFrame", tag)
            .effect("Невозможно разобрать класс \"{0}\"", sourceName)
            .action("Необходимо доработать парсер класса секция StackMapFrame или проверить валидность формата класса \"{0}\"", sourceName)
            .exception();
    }

    private StackMapFrameSameLocalsStackItemStruct parseSameLocalsStackItemFrame(int tag) {
        StackMapFrameSameLocalsStackItemStruct sameLocals = new StackMapFrameSameLocalsStackItemStruct(tag);
        sameLocals.setStack(parseVerificationType());
        return sameLocals;
    }

    private StackMapFrameSameExtendedStruct parseSameFrameExtended() {
        StackMapFrameSameExtendedStruct sameLocalsExtended = new StackMapFrameSameExtendedStruct();
        sameLocalsExtended.setOffsetDelta(stream.u2());
        return sameLocalsExtended;
    }

    private StackMapFrameChopStruct parseChopFrame(int tag) {
        StackMapFrameChopStruct chop = new StackMapFrameChopStruct(tag);
        chop.setOffsetDelta(stream.u2());
        return chop;
    }

    private StackMapFrameSameLocalsStackItemExtendedStruct parseSameLocalsStackItemFrameExtended() {
        StackMapFrameSameLocalsStackItemExtendedStruct sameLocalsExtended = new StackMapFrameSameLocalsStackItemExtendedStruct();
        sameLocalsExtended.setOffsetDelta(stream.u2());
        sameLocalsExtended.setStack(parseVerificationType());
        return sameLocalsExtended;
    }

    private StackMapFrameAppendStruct parseAppendFrame(int tag) {
        StackMapFrameAppendStruct chop = new StackMapFrameAppendStruct(tag);
        chop.setOffsetDelta(stream.u2());
        chop.setLocals(parseVerificationTypes(tag - StackMapFrames.SAME_FRAME_EXTENDED_TAG));
        return chop;
    }

    private StackMapFrameFullStruct parseFullFrame() {
        StackMapFrameFullStruct fullFrame = new StackMapFrameFullStruct();
        fullFrame.setOffsetDelta(stream.u2());
        fullFrame.setNumberOfLocals(stream.u2());
        fullFrame.setLocals(parseVerificationTypes(fullFrame.getNumberOfLocals()));
        fullFrame.setNumberOfStackItems(stream.u2());
        fullFrame.setStack(parseVerificationTypes(fullFrame.getNumberOfStackItems()));
        return fullFrame;
    }

    private VerificationTypeStruct[] parseVerificationTypes(int count) {
        VerificationTypeStruct[] verificationTypes = new VerificationTypeStruct[count];
        for (int index = 0; index < count; index++) {
            verificationTypes[index] = parseVerificationType();
        }
        return verificationTypes;
    }

    private VerificationTypeStruct parseVerificationType() {
        int tag = stream.u1();
        if (VerificationTypes.TOP_VARIABLE_TAG == tag) {
            return VerificationTypes.TOP_VARIABLE;
        }
        if (VerificationTypes.INTEGER_VARIABLE_TAG == tag) {
            return VerificationTypes.INTEGER_VARIABLE;
        }
        if (VerificationTypes.FLOAT_VARIABLE_TAG == tag) {
            return VerificationTypes.FLOAT_VARIABLE;
        }
        if (VerificationTypes.LONG_VARIABLE_TAG == tag) {
            return VerificationTypes.LONG_VARIABLE;
        }
        if (VerificationTypes.DOUBLE_VARIABLE_TAG == tag) {
            return VerificationTypes.DOUBLE_VARIABLE;
        }
        if (VerificationTypes.NULL_VARIABLE_TAG == tag) {
            return VerificationTypes.NULL_VARIABLE;
        }
        if (VerificationTypes.UNINITIALIZED_THIS_VARIABLE_TAG == tag) {
            return VerificationTypes.UNINITIALIZED_THIS_VARIABLE;
        }
        if (VerificationTypes.OBJECT_VARIABLE_TAG == tag) {
            VerificationTypeObjectVariableStruct vtObject = new VerificationTypeObjectVariableStruct();
            vtObject.setCpoolIndex(stream.u2());
            return vtObject;
        }
        if (VerificationTypes.UNINITIALIZED_VARIABLE_TAG == tag) {
            VerificationTypeUninitializedVariableStruct vtuVariable = new VerificationTypeUninitializedVariableStruct();
            vtuVariable.setOffset(stream.u2());
            return vtuVariable;
        }
        throw new ExceptionBuilder()
            .area(ClassFormatException.PARSE_FILE_AREA)
            .reason("Неизвестный tag \"{0}\" в VerificationType", tag)
            .effect("Невозможно разобрать класс \"{0}\"", sourceName)
            .action("Необходимо доработать парсер класса секция VerificationType или проверить валидность формата класса \"{0}\"", sourceName)
            .exception();
    }

    private AttrExceptionsStruct parseExceptionsAttribute() {
        AttrExceptionsStruct attrExceptions = new AttrExceptionsStruct();
        attrExceptions.setNumberOfExceptions(stream.u2());
        attrExceptions.setExceptionIndexTable(stream.u2Array(attrExceptions.getNumberOfExceptions()));
        return attrExceptions;
    }

    private AttrInnerClassesStruct parseInnerClassesAttribute() {
        AttrInnerClassesStruct attrInnerClasses = new AttrInnerClassesStruct();
        attrInnerClasses.setNumberOfClasses(stream.u2());
        attrInnerClasses.setClasses(parseInnerClasses(attrInnerClasses.getNumberOfClasses()));
        return attrInnerClasses;
    }

    private InnerClassStruct[] parseInnerClasses(int count) {
        InnerClassStruct[] innerClasses = new InnerClassStruct[count];
        for (int index = 0; index < count; index++) {
            innerClasses[index] = parseInnerClass();
        }
        return innerClasses;
    }

    private InnerClassStruct parseInnerClass() {
        InnerClassStruct innerClass = new InnerClassStruct();
        innerClass.setInnerClassInfoIndex(stream.u2());
        innerClass.setOuterClassInfoIndex(stream.u2());
        innerClass.setInnerNameIndex(stream.u2());
        innerClass.setInnerClassAccessFlags(AccessFlags.build(stream.u2(), StructType.CLASS_NESTED_INNER));
        detectInnerClassType(innerClass);
        return innerClass;
    }

    private void detectInnerClassType(InnerClassStruct innerClass) {
        StructType classType = StructType.CLASS_NESTED_INNER;
        if (innerClass.getOuterClassInfoIndex() == 0) {
            if (innerClass.getInnerNameIndex() == 0) {
                classType = CLASS_NESTED_ANONYMOUS;
            }
            classType = CLASS_NESTED_LOCAL;
        }
        innerClass.setClassType(classType);
    }

    private AttrEnclosingMethodStruct parseEnclosingMethodAttribute() {
        AttrEnclosingMethodStruct attrEnclosingMethod = new AttrEnclosingMethodStruct();
        attrEnclosingMethod.setClassIndex(stream.u2());
        attrEnclosingMethod.setMethodIndex(stream.u2());
        return attrEnclosingMethod;
    }

    private AttrSyntheticStruct parseSyntheticAttribute() {
        return new AttrSyntheticStruct();
    }

    private AttrSignatureStruct parseSignatureAttribute() {
        AttrSignatureStruct attrSignature = new AttrSignatureStruct();
        attrSignature.setSignatureIndex(stream.u2());
        return attrSignature;
    }

    private AttrSourceFileStruct parseSourceFileAttribute() {
        AttrSourceFileStruct attrSourceFile = new AttrSourceFileStruct();
        attrSourceFile.setSourceFileIndex(stream.u2());
        return attrSourceFile;
    }

    private AttrSourceDebugExtensionStruct parseSourceDebugExtensionAttribute(int length) {
        AttrSourceDebugExtensionStruct attrSourceDebugExtension = new AttrSourceDebugExtensionStruct();
        attrSourceDebugExtension.setData(stream.u1Array(length));
        return attrSourceDebugExtension;
    }

    private AttrLineNumberTableStruct parseLineNumberTableAttribute() {
        AttrLineNumberTableStruct attrLineNumberTable = new AttrLineNumberTableStruct();
        attrLineNumberTable.setLineNumberTableLength(stream.u2());
        attrLineNumberTable.setLineNumberTable(parseLineNumberTables(attrLineNumberTable.getLineNumberTableLength()));
        return attrLineNumberTable;
    }

    private LineNumberTableStruct[] parseLineNumberTables(int count) {
        LineNumberTableStruct[] lineNumbers = new LineNumberTableStruct[count];
        for (int index = 0; index < count; index++) {
            lineNumbers[index] = parseLineNumberTable();
        }
        return lineNumbers;
    }

    private LineNumberTableStruct parseLineNumberTable() {
        LineNumberTableStruct lineNumber = new LineNumberTableStruct();
        lineNumber.setStartPc(stream.u2());
        lineNumber.setLineNumber(stream.u2());
        return lineNumber;
    }

    private AttrLocalVariableTableStruct parseLocalVariableTableAttribute() {
        AttrLocalVariableTableStruct attrLocalVariableTable = new AttrLocalVariableTableStruct();
        attrLocalVariableTable.setLocalVariableTableLength(stream.u2());
        attrLocalVariableTable.setLocalVariableTable(parseLocalVariableTables(attrLocalVariableTable.getLocalVariableTableLength()));
        return attrLocalVariableTable;
    }

    private LocalVariableTableStruct[] parseLocalVariableTables(int count) {
        LocalVariableTableStruct[] localVariables = new LocalVariableTableStruct[count];
        for (int index = 0; index < count; index++) {
            localVariables[index] = parseLocalVariableTable();
        }
        return localVariables;
    }

    private LocalVariableTableStruct parseLocalVariableTable() {
        LocalVariableTableStruct localVariable = new LocalVariableTableStruct();
        localVariable.setStartPc(stream.u2());
        localVariable.setLength(stream.u2());
        localVariable.setNameIndex(stream.u2());
        localVariable.setDescriptorIndex(stream.u2());
        localVariable.setIndex(stream.u2());
        return localVariable;
    }

    private AttrLocalVariableTypeTableStruct parseLocalVariableTypeTableAttribute() {
        AttrLocalVariableTypeTableStruct attrLocalVariableTypeTable = new AttrLocalVariableTypeTableStruct();
        attrLocalVariableTypeTable.setLocalVariableTypeTableLength(stream.u2());
        attrLocalVariableTypeTable.setLocalVariableTypeTable(parseLocalVariableTypeTables(attrLocalVariableTypeTable.getLocalVariableTypeTableLength()));
        return attrLocalVariableTypeTable;
    }

    private LocalVariableTypeTableStruct[] parseLocalVariableTypeTables(int count) {
        LocalVariableTypeTableStruct[] localVariableTypes = new LocalVariableTypeTableStruct[count];
        for (int index = 0; index < count; index++) {
            localVariableTypes[index] = parseLocalVariableTypeTable();
        }
        return localVariableTypes;
    }

    private LocalVariableTypeTableStruct parseLocalVariableTypeTable() {
        LocalVariableTypeTableStruct localVariableType = new LocalVariableTypeTableStruct();
        localVariableType.setStartPc(stream.u2());
        localVariableType.setLength(stream.u2());
        localVariableType.setNameIndex(stream.u2());
        localVariableType.setSignatureIndex(stream.u2());
        localVariableType.setIndex(stream.u2());
        return localVariableType;
    }

    private AttrDeprecatedStruct parseDeprecatedAttribute() {
        return new AttrDeprecatedStruct();
    }

    private AttrRuntimeVisibleAnnotationsStruct parseRuntimeVisibleAnnotationsAttribute() {
        AttrRuntimeVisibleAnnotationsStruct attrAnnotations = new AttrRuntimeVisibleAnnotationsStruct();
        parseRuntimeAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private AttrRuntimeInvisibleAnnotationsStruct parseRuntimeInvisibleAnnotationsAttribute() {
        AttrRuntimeInvisibleAnnotationsStruct attrAnnotations = new AttrRuntimeInvisibleAnnotationsStruct();
        parseRuntimeAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private void parseRuntimeAnnotationsAttribute(AttrRuntimeAnnotationsStruct attrAnnotations) {
        attrAnnotations.setNumAnnotations(stream.u2());
        attrAnnotations.setAnnotations(parseAnnotations(attrAnnotations.getNumAnnotations()));
    }

    private ElementValueAnnotationStruct[] parseAnnotations(int count) {
        ElementValueAnnotationStruct[] annotations = new ElementValueAnnotationStruct[count];
        for (int index = 0; index < count; index++) {
            annotations[index] = parseAnnotation();
        }
        return annotations;
    }

    private ElementValueAnnotationStruct parseAnnotation() {
        ElementValueAnnotationStruct annotation = new ElementValueAnnotationStruct();
        annotation.setTypeIndex(stream.u2());
        annotation.setNumElementValuePairs(stream.u2());
        annotation.setElementValuePairs(parseElementValuePairs(annotation.getNumElementValuePairs()));
        return annotation;
    }

    private ElementValuePairStruct[] parseElementValuePairs(int count) {
        ElementValuePairStruct[] pairs = new ElementValuePairStruct[count];
        for (int index = 0; index < count; index++) {
            pairs[index] = parseElementValuePair();
        }
        return pairs;
    }

    private ElementValuePairStruct parseElementValuePair() {
        ElementValuePairStruct pair = new ElementValuePairStruct();
        pair.setElementNameIndex(stream.u2());
        pair.setElementValue(parseElementValue());
        return pair;
    }

    private ElementValueStruct[] parseElementValues(int count) {
        ElementValueStruct[] elementValues = new ElementValueStruct[count];
        for (int index = 0; index < count; index++) {
            elementValues[index] = parseElementValue();
        }
        return elementValues;
    }

    private ElementValueStruct parseElementValue() {
        char tag = (char) stream.u1();
        if (ElementValues.BYTE_EV == tag) {
            return parseElementValueByteStruct();
        }
        if (ElementValues.CHAR_EV == tag) {
            return parseElementValueCharStruct();
        }
        if (ElementValues.DOUBLE_EV == tag) {
            return parseElementValueDoubleStruct();
        }
        if (ElementValues.FLOAT_EV == tag) {
            return parseElementValueFloatStruct();
        }
        if (ElementValues.INT_EV == tag) {
            return parseElementValueIntStruct();
        }
        if (ElementValues.LONG_EV == tag) {
            return parseElementValueLongStruct();
        }
        if (ElementValues.SHORT_EV == tag) {
            return parseElementValueShortStruct();
        }
        if (ElementValues.BOOLEAN_EV == tag) {
            return parseElementValueBooleanStruct();
        }
        if (ElementValues.STRING_EV == tag) {
            return parseElementValueStringStruct();
        }
        if (ElementValues.ENUM_EV == tag) {
            return parseElementValueEnumConstStruct();
        }
        if (ElementValues.CLASS_EV == tag) {
            return parseElementValueClassIndexStruct();
        }
        if (ElementValues.ANNOTATION_EV == tag) {
            return parseAnnotation();
        }
        if (ElementValues.ARRAY_EV == tag) {
            return parseElementValueArrayStruct();
        }

        throw new ExceptionBuilder()
            .area(ClassFormatException.PARSE_FILE_AREA)
            .reason("Неизвестный tag \"{0}\" в ElementValue", tag)
            .effect("Невозможно разобрать класс \"{0}\"", sourceName)
            .action("Необходимо доработать парсер класса секция ElementValue или проверить валидность формата класса \"{0}\"", sourceName)
            .exception();

    }

    private ElementValueByteStruct parseElementValueByteStruct() {
        ElementValueByteStruct byteValue = new ElementValueByteStruct();
        byteValue.setValue(classFile.getConstantPool().getByteValue(stream.u2()));
        return byteValue;
    }

    private ElementValueCharStruct parseElementValueCharStruct() {
        ElementValueCharStruct charValue = new ElementValueCharStruct();
        charValue.setValue(classFile.getConstantPool().getCharValue(stream.u2()));
        return charValue;
    }

    private ElementValueDoubleStruct parseElementValueDoubleStruct() {
        ElementValueDoubleStruct doubleValue = new ElementValueDoubleStruct();
        doubleValue.setValue(classFile.getConstantPool().getDoubleValue(stream.u2()));
        return doubleValue;
    }

    private ElementValueFloatStruct parseElementValueFloatStruct() {
        ElementValueFloatStruct floatValue = new ElementValueFloatStruct();
        floatValue.setValue(classFile.getConstantPool().getFloatValue(stream.u2()));
        return floatValue;
    }

    private ElementValueIntStruct parseElementValueIntStruct() {
        ElementValueIntStruct intValue = new ElementValueIntStruct();
        intValue.setValue(classFile.getConstantPool().getIntValue(stream.u2()));
        return intValue;
    }

    private ElementValueLongStruct parseElementValueLongStruct() {
        ElementValueLongStruct longValue = new ElementValueLongStruct();
        longValue.setValue(classFile.getConstantPool().getLongValue(stream.u2()));
        return longValue;
    }

    private ElementValueShortStruct parseElementValueShortStruct() {
        ElementValueShortStruct shortValue = new ElementValueShortStruct();
        shortValue.setValue(classFile.getConstantPool().getShortValue(stream.u2()));
        return shortValue;
    }

    private ElementValueBooleanStruct parseElementValueBooleanStruct() {
        ElementValueBooleanStruct booleanValue = new ElementValueBooleanStruct();
        booleanValue.setValue(classFile.getConstantPool().getBooleanValue(stream.u2()));
        return booleanValue;
    }

    private ElementValueStringStruct parseElementValueStringStruct() {
        ElementValueStringStruct stringValue = new ElementValueStringStruct();
        stringValue.setValue(classFile.getConstantPool().getStringValue(stream.u2()));
        return stringValue;
    }

    private ElementValueEnumConstStruct parseElementValueEnumConstStruct() {
        ElementValueEnumConstStruct enumValue = new ElementValueEnumConstStruct();
        enumValue.setTypeNameIndex(stream.u2());
        enumValue.setConstNameIndex(stream.u2());
        return enumValue;
    }

    private ElementValueClassIndexStruct parseElementValueClassIndexStruct() {
        ElementValueClassIndexStruct classValue = new ElementValueClassIndexStruct();
        classValue.setClassIndex(stream.u2());
        return classValue;
    }

    private ElementValueArrayStruct parseElementValueArrayStruct() {
        ElementValueArrayStruct arrayValue = new ElementValueArrayStruct();
        arrayValue.setNumValues(stream.u2());
        arrayValue.setValues(parseElementValues(arrayValue.getNumValues()));
        return arrayValue;
    }

    private AttrRuntimeVisibleParameterAnnotationsStruct parseRuntimeVisibleParameterAnnotationsAttribute() {
        AttrRuntimeVisibleParameterAnnotationsStruct attrAnnotations = new AttrRuntimeVisibleParameterAnnotationsStruct();
        parseRuntimeParameterAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private AttrRuntimeInvisibleParameterAnnotationsStruct parseRuntimeInvisibleParameterAnnotationsAttribute() {
        AttrRuntimeInvisibleParameterAnnotationsStruct attrAnnotations = new AttrRuntimeInvisibleParameterAnnotationsStruct();
        parseRuntimeParameterAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private void parseRuntimeParameterAnnotationsAttribute(AttrRuntimeParameterAnnotationsStruct attrAnnotations) {
        attrAnnotations.setNumParameters(stream.u1());
        attrAnnotations.setParameterAnnotations(parseParameterAnnotations(attrAnnotations.getNumParameters()));
    }

    private ParameterAnnotationStruct[] parseParameterAnnotations(int count) {
        ParameterAnnotationStruct[] parameterAnnotations = new ParameterAnnotationStruct[count];
        for (int index = 0; index < count; index++) {
            parameterAnnotations[index] = parseParameterAnnotation();
        }
        return parameterAnnotations;
    }

    private ParameterAnnotationStruct parseParameterAnnotation() {
        ParameterAnnotationStruct parameterAnnotation = new ParameterAnnotationStruct();
        parameterAnnotation.setNumAnnotations(stream.u2());
        parameterAnnotation.setAnnotations(parseAnnotations(parameterAnnotation.getNumAnnotations()));
        return parameterAnnotation;
    }

    private AttrRuntimeVisibleTypeAnnotationsStruct parseRuntimeVisibleTypeAnnotationsAttribute() {
        AttrRuntimeVisibleTypeAnnotationsStruct attrAnnotations = new AttrRuntimeVisibleTypeAnnotationsStruct();
        parseRuntimeTypeAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private AttrRuntimeInvisibleTypeAnnotationsStruct parseRuntimeInvisibleTypeAnnotationsAttribute() {
        AttrRuntimeInvisibleTypeAnnotationsStruct attrAnnotations = new AttrRuntimeInvisibleTypeAnnotationsStruct();
        parseRuntimeTypeAnnotationsAttribute(attrAnnotations);
        return attrAnnotations;
    }

    private void parseRuntimeTypeAnnotationsAttribute(AttrRuntimeTypeAnnotationsStruct attrAnnotations) {
        attrAnnotations.setNumAnnotations(stream.u2());
        attrAnnotations.setAnnotations(parseTypeAnnotations(attrAnnotations.getNumAnnotations()));
    }

    private TypeAnnotationStruct[] parseTypeAnnotations(int count) {
        TypeAnnotationStruct[] typeAnnotations = new TypeAnnotationStruct[count];
        for (int index = 0; index < count; index++) {
            typeAnnotations[index] = parseTypeAnnotation();
        }
        return typeAnnotations;
    }

    private TypeAnnotationStruct parseTypeAnnotation() {
        TypeAnnotationStruct typeAnnotation = new TypeAnnotationStruct();
        typeAnnotation.setTargetType(parseTargetInfo());
        typeAnnotation.setTypePath(parseTypePath());
        typeAnnotation.setAnnotation(parseAnnotation());
        return typeAnnotation;
    }

    private TargetInfoStruct parseTargetInfo() {
        int tag = stream.u1();
        if (TargetInfos.isTypeParameterTarget(tag)) {
            return parseTypeParameterTarget(tag);
        }
        if (TargetInfos.isSupertypeTarget(tag)) {
            return parseSupertypeTarget();
        }
        if (TargetInfos.isTypeParameterBoundTarget(tag)) {
            return parseTypeParameterBoundTarget(tag);
        }
        if (TargetInfos.isEmptyTarget(tag)) {
            return parseEmptyTarget(tag);
        }
        if (TargetInfos.isFormalParameterTarget(tag)) {
            return parseFormalParameterTarget();
        }
        if (TargetInfos.isThrowsTarget(tag)) {
            return parseThrowsTarget();
        }
        if (TargetInfos.isLocalvarTarget(tag)) {
            return parseLocalvarTarget(tag);
        }
        if (TargetInfos.isCatchTarget(tag)) {
            return parseCatchTarget();
        }
        if (TargetInfos.isOffsetTarget(tag)) {
            return parseOffsetTarget(tag);
        }
        if (TargetInfos.isTypeArgumentTarget(tag)) {
            return parseTypeArgumentTarget(tag);
        }
        throw new ExceptionBuilder()
            .area(ClassFormatException.PARSE_FILE_AREA)
            .reason("Неизвестный tag \"{0}\" в TargetInfo", tag)
            .effect("Невозможно разобрать класс \"{0}\"", sourceName)
            .action("Необходимо доработать парсер класса секция TargetInfo или проверить валидность формата класса \"{0}\"", sourceName)
            .exception();
    }

    private TargetInfoTypeParameterStruct parseTypeParameterTarget(int tag) {
        TargetInfoTypeParameterStruct targetInfo = new TargetInfoTypeParameterStruct(tag);
        targetInfo.setTypeParameterIndex(stream.u1());
        return targetInfo;
    }

    private TargetInfoSupertypeStruct parseSupertypeTarget() {
        TargetInfoSupertypeStruct targetInfo = new TargetInfoSupertypeStruct();
        targetInfo.setSupertypeIndex(stream.u2());
        return targetInfo;
    }

    private TargetInfoTypeParameterBoundStruct parseTypeParameterBoundTarget(int tag) {
        TargetInfoTypeParameterBoundStruct targetInfo = new TargetInfoTypeParameterBoundStruct(tag);
        targetInfo.setTypeParameterIndex(stream.u1());
        targetInfo.setBoundIndex(stream.u1());
        return targetInfo;
    }

    private TargetInfoEmptyStruct parseEmptyTarget(int tag) {
        return new TargetInfoEmptyStruct(tag);
    }

    private TargetInfoFormalParameterStruct parseFormalParameterTarget() {
        TargetInfoFormalParameterStruct targetInfo = new TargetInfoFormalParameterStruct();
        targetInfo.setFormalParameterIndex(stream.u1());
        return targetInfo;
    }

    private TargetInfoThrowsStruct parseThrowsTarget() {
        TargetInfoThrowsStruct targetInfo = new TargetInfoThrowsStruct();
        targetInfo.setThrowsTypeIndex(stream.u2());
        return targetInfo;
    }

    private TargetInfoLocalvarStruct parseLocalvarTarget(int tag) {
        TargetInfoLocalvarStruct targetInfo = new TargetInfoLocalvarStruct(tag);
        targetInfo.setTableLength(stream.u2());
        targetInfo.setTables(parseLocalvarTables(targetInfo.getTableLength()));
        return targetInfo;
    }

    private LocalvarTableStruct[] parseLocalvarTables(int count) {
        LocalvarTableStruct[] localvarTables = new LocalvarTableStruct[count];
        for (int index = 0; index < count; index++) {
            localvarTables[index] = parseLocalvarTable();
        }
        return localvarTables;
    }

    private LocalvarTableStruct parseLocalvarTable() {
        LocalvarTableStruct localvarTable = new LocalvarTableStruct();
        localvarTable.setStartPc(stream.u2());
        localvarTable.setLength(stream.u2());
        localvarTable.setIndex(stream.u2());
        return localvarTable;
    }

    private TargetInfoCatchStruct parseCatchTarget() {
        TargetInfoCatchStruct targetInfo = new TargetInfoCatchStruct();
        targetInfo.setExceptionTableIndex(stream.u2());
        return targetInfo;
    }

    private TargetInfoOffsetStruct parseOffsetTarget(int tag) {
        TargetInfoOffsetStruct targetInfo = new TargetInfoOffsetStruct(tag);
        targetInfo.setOffset(stream.u2());
        return targetInfo;
    }

    private TargetInfoTypeArgumentStruct parseTypeArgumentTarget(int tag) {
        TargetInfoTypeArgumentStruct targetInfo = new TargetInfoTypeArgumentStruct(tag);
        targetInfo.setOffset(stream.u2());
        targetInfo.setTypeArgumentIndex(stream.u1());
        return targetInfo;
    }

    private TypePathStruct parseTypePath() {
        TypePathStruct typePath = new TypePathStruct();
        typePath.setPathLength(stream.u1());
        typePath.setPathes(parsePathes(typePath.getPathLength()));
        return typePath;
    }

    private PathStruct[] parsePathes(int count) {
        PathStruct[] pathes = new PathStruct[count];
        for (int index = 0; index < count; index++) {
            pathes[index] = parsePath();
        }
        return pathes;
    }

    private PathStruct parsePath() {
        PathStruct path = new PathStruct();
        path.setTypePathKind(stream.u1());
        path.setTypeArgumentIndex(stream.u1());
        return path;
    }

    private AttrAnnotationDefaultStruct parseAnnotationDefaultAttribute() {
        AttrAnnotationDefaultStruct attrAnnotationDefault = new AttrAnnotationDefaultStruct();
        attrAnnotationDefault.setElementValue(parseElementValue());
        return attrAnnotationDefault;
    }

    private AttrMethodParametersStruct parseMethodParametersAttribute() {
        AttrMethodParametersStruct attrMethodParameters = new AttrMethodParametersStruct();
        attrMethodParameters.setParametersCount(stream.u1());
        attrMethodParameters.setParameters(parseParameteres(attrMethodParameters.getParametersCount()));
        return attrMethodParameters;
    }

    private ParameterStruct[] parseParameteres(int count) {
        ParameterStruct[] parameters = new ParameterStruct[count];
        for (int index = 0; index < count; index++) {
            parameters[index] = parseParameter();
        }
        return parameters;
    }

    private ParameterStruct parseParameter() {
        ParameterStruct parameter = new ParameterStruct();
        parameter.setNameIndex(stream.u2());
        parameter.setAccessFlags(AccessFlags.build(stream.u2(), StructType.PARAM));
        return parameter;
    }

    private AttrBootstrapMethodsStruct parseBootstrapMethodsAttribute() {
        AttrBootstrapMethodsStruct bootMethodsAttr = new AttrBootstrapMethodsStruct();
        bootMethodsAttr.setNumBootstrapMethods(stream.u2());
        bootMethodsAttr.setBootstrapMethods(parseBootstrapMethods(bootMethodsAttr.getNumBootstrapMethods()));
        return bootMethodsAttr;
    }

    private BootstrapMethodStruct[] parseBootstrapMethods(int count) {
        BootstrapMethodStruct[] bootMethods = new BootstrapMethodStruct[count];
        for (int index = 0; index < count; index++) {
            bootMethods[index] = parseBootstrapMethod();
        }
        return bootMethods;
    }

    private BootstrapMethodStruct parseBootstrapMethod() {
        BootstrapMethodStruct bootMethod = new BootstrapMethodStruct();
        bootMethod.setBootstrapMethodRef(stream.u2());
        bootMethod.setNumBootstrapArguments(stream.u2());
        bootMethod.setBootstrapArguments(stream.u2Array(bootMethod.getNumBootstrapArguments()));
        return bootMethod;
    }

    private AttrUnknownStruct parseUnknownAttribute(int length) {
        AttrUnknownStruct attrUnknown = new AttrUnknownStruct();
        attrUnknown.setData(stream.u1Array(length));
        return attrUnknown;
    }

}

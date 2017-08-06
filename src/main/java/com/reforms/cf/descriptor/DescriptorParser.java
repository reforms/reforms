package com.reforms.cf.descriptor;


import com.reforms.cf.ClassFormatException;
import com.reforms.cf.ExceptionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The parser for descriptors
 * @author evgenie
 *
 */
public class DescriptorParser {

    public static Descriptor parseDescriptor(String descriptor) {
        if (descriptor.indexOf('(') != -1) {
            return parseMethodDescriptor(descriptor);
        }
        return parseFieldDescriptor(descriptor);
    }

    public static FieldDescriptor parseBinnaryClassName(String binnaryClassName) {
        // Для массивов указывается полный дескриптор класса
        if (Descriptors.ARRAY_TYPE == binnaryClassName.charAt(0) || binnaryClassName.endsWith(";")) {
            return parseFieldDescriptor(binnaryClassName);
        }
        // Для классов примитивов и v.class
        if (binnaryClassName.length() == 1) {
            char term = binnaryClassName.charAt(0);
            if (Descriptors.isBaseTerm(term)) {
                return Descriptors.getBaseTerm(term);
            }
            if (Descriptors.isVoidTerm(term)) {
                return Descriptors.VOID;
            }
        }
        return parseFieldDescriptor(Descriptors.CLASS_NAME_TYPE + binnaryClassName + ";");
    }

    public static FieldDescriptor parseFieldDescriptor(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) {
            throw makeEmptyException(descriptor, "FieldDescriptor");
        }
        FieldDescriptor cachedDescriptor = Descriptors.getFieldDescriptor(descriptor);
        if (cachedDescriptor != null) {
            return cachedDescriptor;
        }
        char term = descriptor.charAt(0);
        if (Descriptors.isBaseTerm(term)) {
            return Descriptors.getBaseTerm(term);
        }
        if (Descriptors.isVoidTerm(term)) {
            return Descriptors.VOID;
        }
        if (Descriptors.isObjectTerm(term)) {
            int semicolonIndex = descriptor.indexOf(';');
            if (semicolonIndex == -1 || semicolonIndex == 1) {
                throw makeInvalidDescriptorException(descriptor, "ObjectType");
            }
            String binnaryClassName = descriptor.substring(1, semicolonIndex);
            return new ObjectType(binnaryClassName, parseClassName(binnaryClassName));
        }
        if (Descriptors.isArrayTerm(term)) {
            int demension = 1;
            while (descriptor.length() > demension && Descriptors.isArrayTerm(descriptor.charAt(demension))) {
                demension++;
            }
            if (descriptor.length() > demension) {
                FieldDescriptor componentType = parseFieldDescriptor(descriptor.substring(1));
                FieldDescriptor lowerType = demension == 1 ? componentType : ((ArrayType) componentType).getLowerType();
                return new ArrayType(componentType, lowerType, demension);
            }
        }
        throw makeInvalidDescriptorException(descriptor, "FieldDescriptor");
    }

    private static FieldDescriptor[] EMPTY_FIELD_DESCRIPTORS = new FieldDescriptor[0];

    public static MethodDescriptor parseMethodDescriptor(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) {
            throw makeEmptyException(descriptor, "MethodDescriptor");
        }
        MethodDescriptor cachedDescriptor = Descriptors.getMethodDescriptor(descriptor);
        if (cachedDescriptor != null) {
            return cachedDescriptor;
        }
        if ('(' == descriptor.charAt(0) && descriptor.length() > 2) {
            List<FieldDescriptor> params = Collections.<FieldDescriptor>emptyList();
            int index = 1;
            while (descriptor.length() > index && ')' != descriptor.charAt(index)) {
                FieldDescriptor param = parseFieldDescriptor(descriptor.substring(index));
                index += param.getDescriptor().length();
                if (params.isEmpty()) {
                    params = new ArrayList<>();
                }
                params.add(param);
            }
            index++;
            if (descriptor.length() > index) {
                FieldDescriptor returnType = parseFieldDescriptor(descriptor.substring(index));
                return new MethodDescriptor(params.toArray(EMPTY_FIELD_DESCRIPTORS), returnType);
            }

        }
        throw makeInvalidDescriptorException(descriptor, "MethodDescriptor");
    }

    private static ClassFormatException makeEmptyException(String descriptor, String descriptorType) {
        return new ExceptionBuilder()
            .area(ClassFormatException.PARSE_DESCRIPTOR_AREA)
            .reason("Значение {0}-дескриптора не может быть пустым или null", descriptorType)
            .effect("Невозможно распарсить дескриптор \"{0}\"", descriptor == null ? "null" : descriptor)
            .action("Необходимо проверить логику получения значения дескриптора в месте вызова")
            .exception();
    }

    private static ClassFormatException makeInvalidDescriptorException(String descriptor, String descriptorType) {
        return new ExceptionBuilder()
            .area(ClassFormatException.PARSE_DESCRIPTOR_AREA)
            .reason("Значение {0}-дескриптора не является валидным", descriptorType)
            .effect("Невозможно распарсить дескриптор \"{0}\"", descriptor)
            .action("Необходимо проверить логику получения значения дескриптора в месте вызова")
            .exception();
    }

    private static String parseClassName(String binnaryClassName) {
        int slashIndex = binnaryClassName.lastIndexOf('/');
        if (slashIndex == -1) {
            return binnaryClassName;
        }
        return binnaryClassName.substring(slashIndex + 1);
    }

}
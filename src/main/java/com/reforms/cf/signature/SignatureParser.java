package com.reforms.cf.signature;

import com.reforms.cf.ClassFormatException;
import com.reforms.cf.ExceptionBuilder;
import com.reforms.cf.descriptor.Descriptors;

import java.util.ArrayList;
import java.util.List;


/**
 * The parser for signatures
 * @author evgenie
 */
public class SignatureParser {

    private String content;
    private int index;
    private String typeName;

    public ClassSignature parseClassSignature(String content) {
        this.content = content;
        index = 0;
        typeName = "CLASS_SIGNATURE";
        TypeParameter[] typeParameters = parseTypeParameters();
        ClassTypeSignature superclass = parseClassTypeSignature();
        ClassTypeSignature[] superInterfaces = parseClassTypeSignatures();
        return new ClassSignature(typeParameters, superclass, superInterfaces);
    }

    public MethodSignature parseMethodSignature(String content) {
        this.content = content;
        index = 0;
        typeName = "METHOD_SIGNATURE";
        TypeParameter[] typeParameters = parseTypeParameters();
        if ('(' != symbol()) {
            throw makeInvalidSignatureException("MethodSignature");
        }
        index++;
        Signature[] javaTypes = parseJavaTypeSignatures();
        if (')' != symbol()) {
            throw makeInvalidSignatureException("MethodSignature");
        }
        index++;
        Signature result = parseResult();
        ThrowsSignature[] throwsSignatures = parseThrowsSignatures();
        return new MethodSignature(typeParameters, javaTypes, result, throwsSignatures);
    }

    public Signature parseFieldSignature(String content) {
        this.content = content;
        index = 0;
        typeName = "FIELD_SIGNATURE";
        return parseReferenceTypeSignature();
    }

    private static final TypeParameter[] EMPTY_TYPE_PARAMETERS = new TypeParameter[] {};

    private TypeParameter[] parseTypeParameters() {
        if (symbol() == '<') {
            index++;
            List<TypeParameter> typeParameters = new ArrayList<>();
            while (symbol() != '>') {
                if (symbol() == EOS) {
                    throw makeInvalidSignatureException("TypeParameters");
                }
                TypeParameter typeParameter = parseTypeParameter();
                if (typeParameter == null) {
                    throw makeInvalidSignatureException("TypeParameters");
                }
                typeParameters.add(typeParameter);
            }
            index++;
            return typeParameters.toArray(EMPTY_TYPE_PARAMETERS);
        }
        return EMPTY_TYPE_PARAMETERS;
    }

    private TypeParameter parseTypeParameter() {
        String identifier = parseIdentifier();
        Signature classBound = parseReferenceTypeSignatureBound(true);
        Signature[] interfacesBound = parseReferenceTypeSignatureBounds();
        return new TypeParameter(identifier, classBound, interfacesBound);
    }

    private String parseIdentifier() {
        int fromIndex = index;
        while (isIdentifierSymbol()) {
            index++;
        }
        if (fromIndex == index || symbol() == EOS) {
            throw makeInvalidSignatureException("Identifier");
        }
        return content.substring(fromIndex, index);
    }

    private boolean isIdentifierSymbol() {
        char symbol = symbol();
        return '.' != symbol && ';' != symbol && '[' != symbol && '/' != symbol && '<' != symbol && '>' != symbol &&
                ':' != symbol && EOS != symbol;
    }

    private static final Signature[] EMPTY_SIGNATURES = new Signature[] {};

    private Signature[] parseReferenceTypeSignatureBounds() {
        Signature signature = parseReferenceTypeSignatureBound(false);
        if (signature != null) {
            List<Signature> signatures = new ArrayList<Signature>();
            do {
                signatures.add(signature);
                signature = parseReferenceTypeSignatureBound(false);
            } while (signature != null);
            return signatures.toArray(EMPTY_SIGNATURES);
        }
        return EMPTY_SIGNATURES;
    }

    private Signature parseReferenceTypeSignatureBound(boolean colonRequired) {
        if (symbol() != ':') {
            if (colonRequired) {
                throw makeInvalidSignatureException("ClassBound");
            }
            return null;
        }
        index++;
        char symbol = symbol();
        if (colonRequired && symbol == ':') {
            return null;
        }
        return parseReferenceTypeSignature();
    }

    private Signature parseReferenceTypeSignature() {
        char symbol = symbol();
        if (symbol == Descriptors.CLASS_NAME_TYPE) {
            return parseClassTypeSignature();
        }
        if (symbol == 'T') {
            return parseTypeVariableSignature();
        }
        if (symbol == Descriptors.ARRAY_TYPE) {
            return parseArrayTypeSignature();
        }
        throw makeInvalidSignatureException("ReferenceTypeSignature");
    }

    private static final ClassTypeSignature[] EMPTY_CLASS_TYPE_SIGNATURES = new ClassTypeSignature[] {};

    private ClassTypeSignature[] parseClassTypeSignatures() {
        if (symbol() == Descriptors.CLASS_NAME_TYPE) {
            List<ClassTypeSignature> classTypeSignatures = new ArrayList<>();
            do {
                ClassTypeSignature classTypeSignature = parseClassTypeSignature();
                classTypeSignatures.add(classTypeSignature);
            } while (symbol() == Descriptors.CLASS_NAME_TYPE);
            return classTypeSignatures.toArray(EMPTY_CLASS_TYPE_SIGNATURES);
        }
        return EMPTY_CLASS_TYPE_SIGNATURES;
    }

    private ClassTypeSignature parseClassTypeSignature() {
        index++;
        String packageSpecifier = parsePackageSpecifier();
        SimpleClassTypeSignature simpleClassType = parseSimpleClassTypeSignatureFull(packageSpecifier);
        SimpleClassTypeSignature[] suffixes = parseClassTypeSignatureSuffixes();
        if (';' != symbol()) {
            throw makeInvalidSignatureException("ClassTypeSignature");
        }
        index++;
        return new ClassTypeSignature(packageSpecifier, simpleClassType, suffixes);
    }

    private static final String EMPTY_PACKAGE_SPECIFIER = "";

    private String parsePackageSpecifier() {
        int fromIndex = index;
        while (isIdentifierSymbol()) {
            index++;
        }
        if (symbol() == '/') {
            index++;
            return content.substring(fromIndex, index) + parsePackageSpecifier();
        }
        index = fromIndex;
        return EMPTY_PACKAGE_SPECIFIER;
    }

    private SimpleClassTypeSignature parseSimpleClassTypeSignature() {
        String identifier = parseIdentifier();
        TypeArgument[] typeArguments = parseTypeArguments();
        return new SimpleClassTypeSignature(identifier, identifier, typeArguments);
    }

    private SimpleClassTypeSignature parseSimpleClassTypeSignatureFull(String packageIdentifier) {
        String identifier = parseIdentifier();
        TypeArgument[] typeArguments = parseTypeArguments();
        String shortIdenitifier = parseClassName(packageIdentifier + identifier);
        return new SimpleClassTypeSignature(identifier, shortIdenitifier, typeArguments);
    }

    private static final TypeArgument[] EMPTY_TYPE_ARGUMENTS = new TypeArgument[] {};

    private TypeArgument[] parseTypeArguments() {
        if ('<' == symbol()) {
            index++;
            List<TypeArgument> typeArguments = new ArrayList<>();
            while ('>' != symbol()) {
                if (EOS == symbol()) {
                    throw makeInvalidSignatureException("TypeArguments");
                }
                TypeArgument typeArgument = parseTypeArgument();
                typeArguments.add(typeArgument);
            }
            index++;
            return typeArguments.toArray(EMPTY_TYPE_ARGUMENTS);
        }
        return EMPTY_TYPE_ARGUMENTS;
    }

    private TypeArgument parseTypeArgument() {
        char wildcardIndicator = parseWildcardIndicator();
        Signature signature = Signatures.STAR_WILDCARD_INDICATOR != wildcardIndicator ? parseReferenceTypeSignature()
                : null;
        return new TypeArgument(wildcardIndicator, signature);
    }

    private char parseWildcardIndicator() {
        char symbol = symbol();
        if (Signatures.MINUS_WILDCARD_INDICATOR == symbol || Signatures.PLUS_WILDCARD_INDICATOR == symbol ||
                Signatures.STAR_WILDCARD_INDICATOR == symbol) {
            index++;
            return symbol;
        }
        return Signatures.ABSENT_WILDCARD_INDICATOR;
    }

    private static final SimpleClassTypeSignature[] EMPTY_SIMPLE_CLASS_TYPE_SIGNATURES = new SimpleClassTypeSignature[] {};

    private SimpleClassTypeSignature[] parseClassTypeSignatureSuffixes() {
        if ('.' == symbol()) {
            List<SimpleClassTypeSignature> simpleClassTypeSignatures = new ArrayList<>();
            do {
                index++;
                if (EOS == symbol()) {
                    throw makeInvalidSignatureException("ClassTypeSignatureSuffixes");
                }
                SimpleClassTypeSignature simpleClassTypeSignature = parseSimpleClassTypeSignature();
                simpleClassTypeSignatures.add(simpleClassTypeSignature);
            } while ('.' == symbol());
            return simpleClassTypeSignatures.toArray(EMPTY_SIMPLE_CLASS_TYPE_SIGNATURES);
        }
        return EMPTY_SIMPLE_CLASS_TYPE_SIGNATURES;
    }

    private TypeVariableSignature parseTypeVariableSignature() {
        index++;
        String identifier = parseIdentifier();
        if (';' != symbol()) {
            throw makeInvalidSignatureException("TypeVariableSignature");
        }
        index++;
        return new TypeVariableSignature(identifier);
    }

    private ArrayTypeSignature parseArrayTypeSignature() {
        index++;
        return new ArrayTypeSignature(parseJavaTypeSignature());
    }

    private Signature[] parseJavaTypeSignatures() {
        if (isJavaTypeSignatureSymbol()) {
            List<Signature> signatures = new ArrayList<>();
            do {
                signatures.add(parseJavaTypeSignature());
            } while (isJavaTypeSignatureSymbol());
            return signatures.toArray(EMPTY_SIGNATURES);
        }
        return EMPTY_SIGNATURES;
    }

    private boolean isJavaTypeSignatureSymbol() {
        char symbol = symbol();
        return Descriptors.isBaseTerm(symbol) || Descriptors.CLASS_NAME_TYPE == symbol || 'T' == symbol ||
                Descriptors.ARRAY_TYPE == symbol;
    }

    private Signature parseJavaTypeSignature() {
        char symbol = symbol();
        if (Descriptors.isBaseTerm(symbol)) {
            index++;
            return Descriptors.getBaseTerm(symbol);
        }
        return parseReferenceTypeSignature();
    }

    private Signature parseResult() {
        if (isJavaTypeSignatureSymbol()) {
            return parseJavaTypeSignature();
        }
        if (Descriptors.VOID_TYPE == symbol()) {
            index++;
            return Descriptors.VOID;
        }
        throw makeInvalidSignatureException("Result");
    }

    private static final ThrowsSignature[] EMPTY_THROW_SIGNATURES = new ThrowsSignature[] {};

    private ThrowsSignature[] parseThrowsSignatures() {
        if ('^' == symbol()) {
            List<ThrowsSignature> throwsSignature = new ArrayList<>();
            do {
                throwsSignature.add(parseThrowsSignature());
            } while ('^' == symbol());
            return throwsSignature.toArray(EMPTY_THROW_SIGNATURES);
        }
        return EMPTY_THROW_SIGNATURES;
    }

    private ThrowsSignature parseThrowsSignature() {
        if ('^' != symbol()) {
            throw makeInvalidSignatureException("ThrowsSignature");
        }
        index++;
        if (Descriptors.CLASS_NAME_TYPE != symbol() && 'T' != symbol()) {
            throw makeInvalidSignatureException("ThrowsSignature");
        }
        return new ThrowsSignature(parseReferenceTypeSignature());
    }

    private static final char EOS = '\0';

    private char symbol() {
        return index < content.length() ? content.charAt(index) : EOS;
    }

    private ClassFormatException makeInvalidSignatureException(String signatureType) {
        return new ExceptionBuilder().area(ClassFormatException.PARSE_SIGNATURE_AREA)
                .reason("Некорректное значение при парсинге \"{0}\" индекс {1}", signatureType, index)
                .effect("Невозможно распарсить \"{0}\" сигнатруру \"{1}\"", typeName, content)
                .action("Необходимо исправить парсер сигнатур или проверить валидность самой сигнатуры").exception();
    }

    private String parseClassName(String binnaryClassName) {
        int slashIndex = binnaryClassName.lastIndexOf('/');
        if (slashIndex == -1) {
            return binnaryClassName;
        }
        return binnaryClassName.substring(slashIndex + 1);
    }
}

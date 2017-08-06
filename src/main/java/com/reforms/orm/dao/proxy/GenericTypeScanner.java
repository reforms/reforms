package com.reforms.orm.dao.proxy;

import com.reforms.ann.ThreadSafe;
import com.reforms.cf.ClassFileParser;
import com.reforms.cf.DataStream;
import com.reforms.cf.signature.*;
import com.reforms.cf.struct.*;
import com.reforms.orm.reflex.IGenericTypeResolver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.reforms.cf.struct.Attributes.SIGNATURE_ATTR;

/**
 * Извлекает информацию о Generic спрятанного в возращаемом типе колекции:
 *
 * Пример (где-то в интерфейсе):
 *
 * public java.util.List<String> loadNames();
 *
 * Для метода loadNames вызов getOrmTypeFromReturnCollectionGeneric вернет java.lang.String
 *
 * @author evgenie
 */
@ThreadSafe
public class GenericTypeScanner implements IGenericTypeResolver {

    private final boolean strictMode;

    private final boolean printError;

    public GenericTypeScanner(boolean strictMode, boolean printError) {
        this.strictMode = strictMode;
        this.printError = printError;
    }

    public List<String> getWords(Map<String, String> data) {
        return null;
    }

    @Override
    public Class<?> getGenericWithCollection(Method method) {
        try {
            if (!Collection.class.isAssignableFrom(method.getReturnType())) {
                return null;
            }

            ClassFileStruct cFile = parseClassFile(method);

            MemberStruct targetMethodStruct = findMemberStruct(method, cFile);
            if (targetMethodStruct == null) {
                return null;
            }
            Attributes methodAttrs = targetMethodStruct.getAttributes();
            AttrSignatureStruct methodSign = methodAttrs.find(SIGNATURE_ATTR);
            if (methodSign == null) {
                return null;
            }

            ConstantPool constantPool = cFile.getConstantPool();
            String sigValue = constantPool.getStringValue(methodSign.getSignatureIndex());
            SignatureParser sParser = new SignatureParser();
            MethodSignature methodSignature = sParser.parseMethodSignature(sigValue);
            String targetClassName = extractClassFromSignature(methodSignature);
            if (targetClassName == null) {
                return null;
            }
            return method.getDeclaringClass().getClassLoader().loadClass(targetClassName);
        } catch (Exception ex) {
            if (strictMode) {
                throw new IllegalStateException(ex);
            }
            if (printError) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private ClassFileStruct parseClassFile(Method method) throws Exception {
        Class<?> interfaze = method.getDeclaringClass();
        String resourceInClassPath = interfaze.getName().replace('.', '/') + ".class";
        try (InputStream classResource = interfaze.getClassLoader().getResourceAsStream(resourceInClassPath)) {
            if (classResource == null) {
                throw new IllegalStateException("Не возможно найти ресурс: " + resourceInClassPath);
            }
            DataStream stream = new DataStream(new DataInputStream(new BufferedInputStream(classResource)));
            ClassFileParser cfParser = new ClassFileParser();
            return cfParser.parse(resourceInClassPath, stream);
        }
    }

    private MemberStruct findMemberStruct(Method method, ClassFileStruct cFile) throws Exception {
        ConstantPool constantPool = cFile.getConstantPool();
        MemberStruct[] methodStructs = cFile.getMethods();
        for (MemberStruct methodStruct : methodStructs) {
            Attributes methodAttrs = methodStruct.getAttributes();
            AttrSignatureStruct methodSign = methodAttrs.find(SIGNATURE_ATTR);
            if (methodSign != null) {
                String methodName = constantPool.getStringValue(methodStruct.getNameIndex());
                if (methodName.equals(method.getName())) {
                    String descriptor = constantPool.getStringValue(methodStruct.getDescriptorIndex());
                    Lookup lk = MethodHandles.lookup();
                    MethodHandle mh = lk.unreflect(method);
                    String fullMDT = mh.type().toMethodDescriptorString();
                    Class<?> orClass = method.getDeclaringClass();
                    String startWith = "(L" + orClass.getName().replace(".", "/") + ";";
                    String shortDesc = fullMDT.replace(startWith, "(");
                    if (shortDesc.equals(descriptor)) {
                        return methodStruct;
                    }
                }
            }
        }
        return null;
    }

    private String extractClassFromSignature(MethodSignature methodSignature) {
        Signature resultSig = methodSignature.getResult();
        if (resultSig instanceof ClassTypeSignature) {
            ClassTypeSignature ctsig = (ClassTypeSignature) resultSig;
            SimpleClassTypeSignature scts = ctsig.getSimpleClassType();
            TypeArgument[] typeArgs = scts.getTypeArguments();
            if (typeArgs != null && typeArgs.length == 1) {
                TypeArgument tArg = typeArgs[0];
                String typeSig = tArg.getSignature().getSignature();
                if (typeSig != null && typeSig.startsWith("L")) {
                    return typeSig.substring(1).replace("/", ".").replace(";", "");
                }
            }
        }
        return null;
    }
}
package com.reforms.orm.reflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.reforms.orm.OrmConfigurator.getInstance;

/**
 * Скан экземпляра, предоставление информации об объекте
 * TODO оптимизация - подумать над оптимизацией
 * @author evgenie
 */
public class Reflexor implements IReflexor {

    private final Map<String, Method> methods = new HashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<String, Class<?>> types = new ConcurrentHashMap<>();
    private final Class<?> instanceClass;

    Reflexor(Class<?> instanceClass) {
        this.instanceClass = instanceClass;
        scanFields(instanceClass);
        scanMethods(instanceClass);
    }

    @Override
    public Class<?> getOrmClass() {
        return instanceClass;
    }

    @Override
    public Object createInstance() {
        return createInstance(instanceClass);
    }

    @Override
    public IInstanceBuilder createInstanceBuilder() {
        // TODO оптимизация: добавить факторию выбора
        return new FullInstanceBuilder(this);
    }

    @Override
    public IInstanceBuilder createInstanceBuilderFor(String fieldName) {
        // TODO оптимизация: добавить факторию выбора
        Class<?> subClass = getType(fieldName);
        return createReflexor(subClass).createInstanceBuilder();
    }

    @Override
    public boolean hasKey(String fieldName) {
        try {
            return getType(fieldName) != null;
        } catch (IllegalStateException mute) {
            // TODO это плохо конечно, но пока так.
        }
        return false;
    }

    @Override
    public Object getValue(Object instance, String metaFieldName) {
        int dotIndex = metaFieldName.indexOf('.');
        if (dotIndex != -1) {
            String fieldName = metaFieldName.substring(0, dotIndex);
            Object value = getValueFrom(instance, fieldName);
            // Фитча опциональности
            if (value == null) {
                return null;
            }
            String subMetaFieldName = metaFieldName.substring(dotIndex + 1);
            IReflexor subReflxor = createReflexor(value.getClass());
            return subReflxor.getValue(value, subMetaFieldName);
        }
        return getValueFrom(instance, metaFieldName);
    }

    @Override
    public Class<?> getType(String metaFieldName) {
        Class<?> classType = types.get(metaFieldName);
        if (classType == null) {
            classType = findType(metaFieldName);
            if (classType != null) {
                types.put(metaFieldName, classType);
            }
        }
        return classType;
    }

    @Override
    public void setValue(Object instance, String metaFieldName, Object value) {
        int dotIndex = metaFieldName.indexOf('.');
        if (dotIndex != -1) {
            String fieldName = metaFieldName.substring(0, dotIndex);
            Object ormValue = getValueFrom(instance, fieldName);
            if (ormValue == null) {
                ormValue = initValue(instance, fieldName);
            }
            String subMetaFieldName = metaFieldName.substring(dotIndex + 1);
            IReflexor subReflxor = createReflexor(ormValue.getClass());
            subReflxor.setValue(ormValue, subMetaFieldName, value);
            return;
        }
        setValueTo(instance, metaFieldName, value);
    }

    private Object getValueFrom(Object instance, String fieldName) {
        String basePartMethodName = makeBasePartMethodName(fieldName);
        String getterMethodName = "get" + basePartMethodName;
        String isMethodName = "is" + basePartMethodName;
        Method method = findMethod(getterMethodName, 0);
        if (method == null) {
            method = findMethod(isMethodName, 0);
        }
        if (method != null) {
            return invokeGetterMethod(instance, method);
        }
        Field field = findField(fieldName);
        if (field != null) {
            return getValueFromField(instance, field);
        }
        throw new IllegalStateException("Не удалось найти поле '" + fieldName + "' или метод(get/is) в объекте класса '" + instanceClass
                + "'");
    }

    private Class<?> findType(String metaFieldName) {
        int dotIndex = metaFieldName.indexOf('.');
        if (dotIndex != -1) {
            String fieldName = metaFieldName.substring(0, dotIndex);
            Class<?> ormClass = getTypeFrom(fieldName);
            String subMetaFieldName = metaFieldName.substring(dotIndex + 1);
            IReflexor subReflxor = createReflexor(ormClass);
            return subReflxor.getType(subMetaFieldName);
        }
        return getTypeFrom(metaFieldName);
    }

    private Class<?> getTypeFrom(String fieldName) {
        String basePartMethodName = makeBasePartMethodName(fieldName);
        String setterMethodName = "set" + basePartMethodName;
        Method setterMethod = findMethod(setterMethodName, 1);
        if (setterMethod != null) {
            return setterMethod.getParameterTypes()[0];
        }
        Field field = findField(fieldName);
        if (field != null) {
            return field.getType();
        }
        throw new IllegalStateException("Не удалось найти поле '" + fieldName + "' или метод(get/is) в объекте класса '" + instanceClass
                + "'");
    }

    private Object initValue(Object instance, String fieldName) {
        Class<?> classType = getTypeFrom(fieldName);
        Object ormInstance = createInstance(classType);
        setValueTo(instance, fieldName, ormInstance);
        return ormInstance;
    }

    private void setValueTo(Object instance, String fieldName, Object value) {
        String basePartMethodName = makeBasePartMethodName(fieldName);
        String setterMethodName = "set" + basePartMethodName;
        Method method = findMethod(setterMethodName, 1);
        if (method != null) {
            invokeSetterMethod(instance, method, value);
            return;
        }
        Field field = findField(fieldName);
        if (field != null) {
            setValueToField(instance, field, value);
            return;
        }
        throw new IllegalStateException("Не удалось найти поле '" + fieldName + "' или метод(get/is) в объекте класса '" + instanceClass
                + "'");
    }

    private Field findField(String fieldName) {
        return fields.get(fieldName);
    }

    private Object getValueFromField(Object instance, Field field) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось получить значение поля '" + field.getName() + "' в объекте класса '"
                    + instanceClass + "'", roe);
        }
    }

    private void setValueToField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось получить значение поля '" + field.getName() + "' в объекте класса '"
                    + instanceClass + "'", roe);
        }
    }

    private Object invokeGetterMethod(Object instance, Method method) {
        try {
            method.setAccessible(true);
            return method.invoke(instance);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось выполнить метод '" + method.getName() + "' в объекте класса '" + instanceClass
                    + "'", roe);
        }
    }

    private void invokeSetterMethod(Object instance, Method method, Object value) {
        try {
            method.setAccessible(true);
            method.invoke(instance, value);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось выполнить метод '" + method.getName() + "' в объекте класса '" + instanceClass
                    + "'", roe);
        }
    }

    private Object createInstance(Class<?> classType) {
        try {
            Constructor<?> constructor = classType.getDeclaredConstructor();
            boolean constructorAccessible = constructor.isAccessible();
            if (!constructorAccessible) {
                constructor.setAccessible(true);
            }
            try {
                return constructor.newInstance();
            } finally {
                if (!constructorAccessible) {
                    constructor.setAccessible(false);
                }
            }
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось создать экземпляр класса '" + classType + "' для объекта класса '" + instanceClass
                    + "'", roe);
        }
    }

    private Method findMethod(String methodName, int paramCount) {
        return methods.get(methodName);
    }

    private String makeBasePartMethodName(String fieldName) {
        char firstLetter = fieldName.charAt(0);
        return String.valueOf(Character.toUpperCase(firstLetter)) + fieldName.substring(1);
    }

    private void scanFields(Class<?> clazz) {
        Field[] classFields = clazz.getDeclaredFields();
        for (Field classField : classFields) {
            int modifier = classField.getModifiers();
            if (!Modifier.isStatic(modifier) && !Modifier.isAbstract(modifier) && !Modifier.isNative(modifier)
                    && !classField.isSynthetic() && !fields.containsKey(classField.getName())) {
                fields.put(classField.getName(), classField);
            }
        }
        Class<?> supperClass = clazz.getSuperclass();
        if (supperClass != null && Object.class != supperClass) {
            scanFields(supperClass);
        }
    }

    private void scanMethods(Class<?> clazz) {
        Method[] classMethods = clazz.getDeclaredMethods();
        for (Method classMethod : classMethods) {
            int modifier = classMethod.getModifiers();
            String methodName = classMethod.getName();
            if (Modifier.isStatic(modifier)
                    || Modifier.isAbstract(modifier)
                    || Modifier.isNative(modifier)
                    || classMethod.isSynthetic()) {
                continue;
            }
            int paramCount = classMethod.getParameterTypes().length;
            Class<?> returnType = classMethod.getReturnType();
            String methodNamePrefix = getMethodNamePrefix(methodName);
            if (("get".equals(methodNamePrefix) || "is".equals(methodNamePrefix) || "has".equals(methodNamePrefix))
                    && paramCount == 0
                    && returnType != void.class
                    && !methods.containsKey(methodName)) {
                methods.put(methodName, classMethod);
                continue;
            }
            if ("set".equals(methodNamePrefix)
                    && paramCount == 1) {
                if (!methods.containsKey(methodName)) {
                    methods.put(methodName, classMethod);
                    continue;
                }
                // Здесь сложнее
                Method beforeMethod = methods.get(methodName);
                Class<?> beforeParamType = beforeMethod.getParameterTypes()[0];
                Class<?> paramType = classMethod.getParameterTypes()[0];
                if (beforeParamType != paramType) {
                    // Какой нафиг приоритетней?
                    // TODO подумать над аннотацией
                    // Вариант N1. Смотрим на поле
                    String fieldName = getFieldNameByMethodName(methodName);
                    Field field = fields.get(fieldName);
                    if (field != null) {
                        if (beforeParamType == field.getType()) {
                            continue;
                        }
                        if (paramType == field.getType()) {
                            methods.put(methodName, classMethod);
                        }
                        continue;
                    }
                    // Вариант N2. Поля нет - подумать, может быть стоит смотреть по set/is/has?
                    continue;
                }
            }
        }
        Class<?> supperClass = clazz.getSuperclass();
        if (supperClass != null && Object.class != supperClass) {
            scanMethods(supperClass);
        }
    }

    private String getMethodNamePrefix(String methodName) {
        for (int index = 0; index < methodName.length(); index++) {
            if (Character.isUpperCase(methodName.charAt(index))) {
                return methodName.substring(0, index);
            }
        }
        return null;
    }

    private String getFieldNameByMethodName(String methodName) {
        for (int index = 0; index < methodName.length(); index++) {
            if (Character.isUpperCase(methodName.charAt(index))) {
                char firstLetter = Character.toLowerCase(methodName.charAt(index));
                return String.valueOf(firstLetter) + methodName.substring(index + 1);
            }
        }
        return null;
    }

    public static IReflexor createReflexor(Class<?> instanceClass) {
        LocalCache localCache = getInstance(LocalCache.class);
        return localCache.getReflexor(instanceClass);
    }

}

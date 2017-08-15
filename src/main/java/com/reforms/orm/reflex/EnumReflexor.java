package com.reforms.orm.reflex;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

import sun.misc.SharedSecrets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.reflex.ClassUtils.isEnumClass;

/**
 * Реализация контракта по работе c enum
 * @author evgenie
 */
public class EnumReflexor implements IEnumReflexor {

    private final Class<?> enumClass;

    private final Field getAssignValueField;

    private final Method getAssignValueMethod;

    private final Method getEnumMethod;

    private Map<Object, Object> enum2Value;
    private Map<Object, Object> value2Enum;
    private Class<?> assignValueClass;

    EnumReflexor(Class<?> enumClass) {
        this.enumClass = enumClass;
        getAssignValueField = findAssignField(enumClass);
        getAssignValueMethod = findAssignMethod(enumClass);
        getEnumMethod = findEnumMethod(enumClass);
        // 1. Этого достаточно, чтобы установить связь
        if (getAssignValueField != null && getAssignValueMethod == null && getEnumMethod == null) {
            Params fieldBaseParams = getParamsForField(getAssignValueField);
            enum2Value = fieldBaseParams.enum2Value;
            value2Enum = fieldBaseParams.value2Enum;
            assignValueClass = fieldBaseParams.assignValueClass;
        }
        // 2. Нет аннотаций вообще. Пытаемся слинковать любое поле по типу из конструктора как наиболее ожидаемое
        if (getAssignValueField == null && getAssignValueMethod == null && getEnumMethod == null) {
            Params autoLinkParams = autoLink();
            enum2Value = autoLinkParams.enum2Value;
            value2Enum = autoLinkParams.value2Enum;
            assignValueClass = autoLinkParams.assignValueClass;
        }
    }

    @Override
    public Object getAssignValue(Object enumValue) {
        if (getAssignValueMethod != null) {
            if (isNeedEnumValue(getAssignValueMethod)) {
                return invokeMethod(getAssignValueMethod, null, enumValue);
            }
            return invokeMethod(getAssignValueMethod, enumValue);
        }
        if (getAssignValueField != null) {
            return getValueFromField(enumValue, getAssignValueField);
        }
        if (enum2Value != null && enum2Value.containsKey(enumValue)) {
            return enum2Value.get(enumValue);
        }
        throw new IllegalStateException("Невозможно получить ассоциированное значение из '" + enumValue + "' в классе '" + enumClass + "'. " +
                "Добавьте аннотацию @TargetField к полю или @TargetMethod к методу (no static) enum, который возращает ассоциированное с ним значение");
    }

    @Override
    public Object getEnumValue(Object assignValue) {
        if (getEnumMethod != null) {
            return invokeMethod(getEnumMethod, null, assignValue);
        }
        if (value2Enum != null && value2Enum.containsKey(assignValue)) {
            return value2Enum.get(assignValue);
        }
        throw new IllegalStateException("Невозможно получить одно из значений перечислений по ассоциированному значению '" + assignValue + "' в классе '" +
                enumClass + "'. Добавьте аннотацию @TargetMethod к методу (static) enum, который возращает enum объект");
    }

    @Override
    public Class<?> getAssignValueClass() {
        if (getEnumMethod != null) {
            return getEnumMethod.getParameterTypes()[0];
        }
        if (getAssignValueField != null) {
            return getAssignValueField.getType();
        }
        if (getAssignValueMethod != null) {
            return getAssignValueMethod.getReturnType();
        }
        if (assignValueClass != null) {
            return assignValueClass;
        }
        throw new IllegalStateException("Невозможно получить тип ассоциированного значения в классе '" + enumClass + "'. " +
                "Добавьте аннотацию @TargetField к полю или @TargetMethod к методу (no static) enum, который возращает ассоциированное с ним значение");
    }

    private Field findAssignField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TargetField.class)) {
                return field;
            }
        }
        Class<?> supperClass = clazz.getSuperclass();
        if (supperClass != null && Object.class != supperClass) {
            return findAssignField(supperClass);
        }
        return null;
    }

    private Method findAssignMethod(Class<?> clazz) {
        Class<?> curClass = clazz;
        while (curClass != null && Object.class != curClass) {
            Method[] methods = curClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(TargetMethod.class)
                        && (isGetter(method) || isNeedEnumValue(method))) {
                    return method;
                }
            }
            curClass = curClass.getSuperclass();
        }
        return null;
    }

    private Method findEnumMethod(Class<?> clazz) {
        Class<?> curClass = clazz;
        while (curClass != null && Object.class != curClass) {
            Method[] methods = curClass.getDeclaredMethods();
            for (Method method : methods) {
                if (isEnumMethod(method)) {
                    return method;
                }
            }
            curClass = curClass.getSuperclass();
        }
        return null;
    }

    private boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0;
    }

    private boolean isNeedEnumValue(Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length == 1) {
            Class<?> paramClass = params[0];
            if (isEnumClass(paramClass)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnumMethod(Method method) {
        if (!method.isAnnotationPresent(TargetMethod.class)) {
            return false;
        }
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            return false;
        }
        Class<?> returnClass = method.getReturnType();
        if (isEnumClass(returnClass)) {
            return true;
        }
        return false;
    }

    private Object getValueFromField(Object instance, Field field) {
        boolean fieldAccessible = field.isAccessible();
        if (!fieldAccessible) {
            field.setAccessible(true);
        }
        try {
            return field.get(instance);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось получить значение поля '" + field.getName() + "' в объекте класса '"
                    + enumClass + "'", roe);
        } finally {
            if (!fieldAccessible) {
                field.setAccessible(fieldAccessible);
            }
        }
    }

    private Object invokeMethod(Method method, Object instance, Object... args) {
        boolean methodAccessible = method.isAccessible();
        if (!methodAccessible) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(instance, args);
        } catch (ReflectiveOperationException roe) {
            throw new IllegalStateException("Не удалось выполнить метод '" + method.getName() + "' в объекте класса '" + enumClass
                    + "'", roe);
        } finally {
            if (!methodAccessible) {
                method.setAccessible(false);
            }
        }
    }

    /**
     * Пытаемся определить используя интелект, какое поле с каким методом косвенно связано?
     * @return связи NOT NULL
     */
    private Params autoLink() {
        // 1. Определим допустимые типы для преобразования
        Set<Class<?>> candidateTypes = getCandidateTypeForAutoLink();
        // 2. Определим допустимые поля для связывания
        Field candidateField = getCandidateFieldAutoLink(candidateTypes);
        if (candidateField != null) {
            return getParamsForField(candidateField);
        }
        // Этот случай меппинг на мена enum
        try {
            return getParamsForField(Enum.class.getDeclaredField("name"));
        } catch (NoSuchFieldException nsfe) {
            throw new IllegalStateException("Not a enum", nsfe);
        }
    }

    private Set<Class<?>> getCandidateTypeForAutoLink() {
        Class<?> scanClass = enumClass;
        // 1. Определим допустимые типы для преобразования
        Set<Class<?>> candidateTypes = new HashSet<Class<?>>();
        while (scanClass != null && scanClass != Enum.class && scanClass != Object.class) {
            for (Constructor<?> constructor : scanClass.getDeclaredConstructors()) {
                // всегда игнорируем 1ый int - это порядковый номер enum но только для enum
                boolean wasInt = !constructor.getDeclaringClass().isEnum();
                // всегда игнорируем 1ый String - это имя для enum но только для enum
                boolean wasString = !constructor.getDeclaringClass().isEnum();
                for (Class<?> paramType : constructor.getParameterTypes()) {
                    // всегда игнорируем 1ый int - это порядковый номер enum но только для enum
                    if (int.class == paramType && !wasInt) {
                        wasInt = true;
                        continue;
                    }
                    // всегда игнорируем 1ый int - это порядковый номер enum но только для enum
                    if (String.class == paramType && !wasString) {
                        wasString = true;
                        continue;
                    }
                    candidateTypes.add(paramType);
                }
            }
            scanClass = scanClass.getSuperclass();
        }
        return candidateTypes;
    }

    private Field getCandidateFieldAutoLink(Set<Class<?>> candidateTypes) {
        Class<?> scanClass = enumClass;
        Field candidateField = null;
        while (scanClass != null && scanClass != Enum.class && scanClass != Object.class && candidateField == null) {
            for (Field field : scanClass.getDeclaredFields()) {
                int modifier = field.getModifiers();
                if (candidateField == null
                        && !Modifier.isStatic(modifier)
                        && candidateTypes.contains(field.getType())
                        && !field.getType().isArray()) {
                    candidateField = field;
                    break;
                }
            }
            scanClass = scanClass.getSuperclass();
        }
        return candidateField;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Params getParamsForField(Field candidateField) {
        Class<?> scanClass = null;
        Map<Object, Object> tmpEnum2Value = new HashMap<>();
        Map<Object, Object> tmpValue2Enum = new HashMap<>();
        // Этот случай совсем хорошо
        scanClass = enumClass;
        if (!scanClass.isEnum()) {
            scanClass = scanClass.getSuperclass();
        }
        Enum<?>[] enumValues = SharedSecrets.getJavaLangAccess().getEnumConstantsShared((Class<Enum>) scanClass);
        // для каждого значения ENUM берем значение его поля
        for (Enum<?> enumValue : enumValues) {
            Object assignValue = getValueFromField(enumValue, candidateField);
            tmpEnum2Value.put(enumValue, assignValue);
            tmpValue2Enum.put(assignValue, enumValue);
        }
        return new Params(tmpEnum2Value, tmpValue2Enum, candidateField.getType());
    }

    public static IEnumReflexor createEnumReflexor(Class<?> instanceClass) {
        LocalCache localCache = getInstance(LocalCache.class);
        return localCache.getEnumReflexor(instanceClass);
    }

    private static class Params {
        private final Map<Object, Object> enum2Value;
        private final Map<Object, Object> value2Enum;
        private final Class<?> assignValueClass;

        Params(Map<Object, Object> enum2Value, Map<Object, Object> value2Enum, Class<?> assignValueClass) {
            this.enum2Value = Collections.unmodifiableMap(enum2Value);
            this.value2Enum = Collections.unmodifiableMap(value2Enum);
            this.assignValueClass = assignValueClass;
        }


    }
}
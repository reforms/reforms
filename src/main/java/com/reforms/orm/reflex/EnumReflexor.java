package com.reforms.orm.reflex;

import static com.reforms.orm.OrmConfigurator.getInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

/**
 * Реализация контракта
 * @author evgenie
 */
public class EnumReflexor implements IEnumReflexor {

    private Class<?> enumClass;

    private Field getAssignValueField;

    private Method getAssignValueMethod;

    private Method getEnumMethod;

    EnumReflexor(Class<?> enumClass) {
        this.enumClass = enumClass;
        getAssignValueField = findAssignField(enumClass);
        getAssignValueMethod = findAssignMethod(enumClass);
        getEnumMethod = findEnumMethod(enumClass);
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
        throw new IllegalStateException("Невозможно получить ассоциированное значение из '" + enumValue + "' в классе '" + enumClass + "'");
    }

    @Override
    public Object getEnumValue(Object assignValue) {
        if (getEnumMethod != null) {
            return invokeMethod(getEnumMethod, null, assignValue);
        }
        throw new IllegalStateException("Невозможно получить одно из значений перечислений по ассоциированному значению '" + assignValue + "' в классе '" +
                enumClass + "'");
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
        throw new IllegalStateException("Невозможно получить тип ассоциированного значения в классе '" + enumClass + "'");
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
            if (paramClass.isEnum()) {
                return true;
            }
            if (paramClass.isAnonymousClass() && paramClass.getSuperclass().isEnum()) {
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
        if (returnClass.isEnum()) {
            return true;
        }
        if (returnClass.isAnonymousClass() && returnClass.getSuperclass().isEnum()) {
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

    private Object invokeMethod(Method method, Object instance, Object ... args) {
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

    public static IEnumReflexor createEnumReflexor(Class<?> instanceClass) {
        LocalCache localCache = getInstance(LocalCache.class);
        return localCache.getEnumReflexor(instanceClass);
    }
}
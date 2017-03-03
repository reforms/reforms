package com.reforms.orm.reflex;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.reforms.orm.reflex.InstanceCreator.createInstanceCreator;

/**
 *
 * @author evgenie
 */
class DefaultValueCreator implements InvocationHandler {

    private static ThreadLocalClassController CLASS_CREATE_CONTROLLER = new ThreadLocalClassController();

    private DefaultValueArray firstValues = new DefaultValueArray();

    private DefaultValueArray secondValues = new DefaultValueArray();

    private final Map<String, Object> typePrimitiveCache = new HashMap<>();

    Object createFirst(Class<?> clazzType) throws Throwable {
        Object result = createDefaultValue(clazzType, 0);
        firstValues.add(result);
        return result;
    }

    Object createSecond(Class<?> clazzType) throws Throwable {
        Object result = createDefaultValue(clazzType, 1);
        secondValues.add(result);
        return result;
    }

    DefaultValueArray getFirstValues() {
        return firstValues;
    }

    DefaultValueArray getSecondValues() {
        return secondValues;
    }

    private Object createDefaultValue(Class<?> clazzType, int index) throws Throwable {
        checkStrongClassType(clazzType);
        Object nextDefaultValue = getNextValue(clazzType, index);
        if (nextDefaultValue != null) {
            return nextDefaultValue;
        }
        Map<Class<?>, Object> stackClasses = CLASS_CREATE_CONTROLLER.get();
        stackClasses.put(clazzType, "");
        try {
            InstanceCreator creator = createInstanceCreator(clazzType);
            InstanceInfo insanceInfo = creator.getFirstInstanceInfo();
            Throwable cause = insanceInfo.getCause();
            if (cause != null) {
                throw cause;
            }
            return index == 0 ? insanceInfo.getInstance1() : insanceInfo.getInstance2();
        } finally {
            stackClasses.remove(clazzType);
        }
    }

    private void checkStrongClassType(Class<?> clazzType) {
        if (clazzType == null) {
            throw new IllegalStateException("Необходимо указать класс, null не допускается");
        }
        if (clazzType.isPrimitive()) {
            return;
        }
        if (Modifier.isAbstract(clazzType.getModifiers()) && !clazzType.isInterface() && !clazzType.isAnnotation()) {
            throw new IllegalStateException("Невозможно создать экземпляр абстрактного класса '" + clazzType + "'");
        }
        if (clazzType == Class.class) {
            throw new IllegalStateException("Невозможно создать экземпляр класса класс '" + clazzType + "'");
        }
        Map<Class<?>, Object> stackClasses = CLASS_CREATE_CONTROLLER.get();
        if (stackClasses.containsKey(clazzType)) {
            throw new RuntimeException("Рекурсия при создании класса '" + clazzType + "'");
        }
    }

    private int booleanCount = 0;

    private Object getNextPrimitiveValue(Class<?> clazzType, int index) {
        String indexedKey = "" + index + "." + clazzType;
        Object previewDefaultValue = typePrimitiveCache.get(indexedKey);
        if (boolean.class == clazzType) {
            if (index == 0) {
                booleanCount++;
                if (booleanCount > 2) {
                    throw new IllegalStateException(
                            "Не возможно определить соответствие между параметром в конструкторе и полем в объекта, если boolean.class в конструкторе больше 2х");
                }
            }
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Boolean(index == 0);
            } else {
                nextValue = new Boolean(!((Boolean) previewDefaultValue).booleanValue());
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        // Всегда делаем отличное значение
        if (index == 1) {
            previewDefaultValue = typePrimitiveCache.get("0." + clazzType);
        }
        if (byte.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Byte((byte) 0);
            } else {
                nextValue = new Byte((byte) (((Byte) previewDefaultValue).byteValue() + 1));
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (short.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Short((short) 0);
            } else {
                nextValue = new Short((short) (((Short) previewDefaultValue).shortValue() + 1));
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (char.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Character('a');
            } else {
                nextValue = new Character((char) (((Character) previewDefaultValue) + 1));
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (int.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Integer(0);
            } else {
                nextValue = new Integer(((Integer) previewDefaultValue) + 1);
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (float.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Float(0.0F);
            } else {
                nextValue = new Float(((Float) previewDefaultValue) + 1.0F);
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (double.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Double(0.0D);
            } else {
                nextValue = new Double(((Double) previewDefaultValue) + 1.0D);
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        if (long.class == clazzType) {
            Object nextValue = null;
            if (previewDefaultValue == null) {
                nextValue = new Long(0L);
            } else {
                nextValue = new Long(((Long) previewDefaultValue) + 1L);
            }
            typePrimitiveCache.put(indexedKey, nextValue);
            return nextValue;
        }
        throw new IllegalStateException("Невозможно получить значение по-умолчанию для примитивного класса '" + clazzType + "'");
    }

    /**
     * TODO оптимизация: переделать на Map и добавить контракт на создание объекта
     * @param clazzType
     * @param index
     * @return
     */
    private Object getNextValue(Class<?> clazzType, int index) {
        if (clazzType.isPrimitive()) {
            return getNextPrimitiveValue(clazzType, index);
        }
        if (Boolean.class == clazzType) {
            return new Boolean(true);
        }
        if (Byte.class == clazzType) {
            return new Byte((byte) 0);
        }
        if (Short.class == clazzType) {
            return new Short((short) 0);
        }
        if (Character.class == clazzType) {
            return new Character('a');
        }
        if (Integer.class == clazzType) {
            return new Integer(0);
        }
        if (Float.class == clazzType) {
            return new Float(0.0F);
        }
        if (Double.class == clazzType) {
            return new Double(0.0D);
        }
        if (Long.class == clazzType) {
            return new Long(0L);
        }
        if (String.class == clazzType) {
            return new String("0");
        }
        if (BigInteger.class == clazzType) {
            return new BigInteger("0");
        }
        if (BigDecimal.class == clazzType) {
            return new BigDecimal("0.0");
        }
        if (java.sql.Date.class == clazzType) {
            return new java.sql.Date(0);
        }
        if (java.sql.Time.class == clazzType) {
            return new java.sql.Time(0);
        }
        if (java.sql.Timestamp.class == clazzType) {
            return new java.sql.Timestamp(0);
        }
        if (java.util.Date.class == clazzType) {
            return new java.util.Date(0);
        }
        if (byte[].class == clazzType) {
            return new byte[] {};
        }
        if (List.class == clazzType) {
            return new ArrayList<>();
        }
        if (Map.class == clazzType) {
            return new HashMap<>();
        }
        if (Queue.class == clazzType) {
            return new ArrayDeque<>();
        }
        if (clazzType.isEnum()) {
            Object[] enumValues = clazzType.getEnumConstants();
            int enumIndex = 0;
            DefaultValueArray values = index == 0 ? firstValues : secondValues;
            while (enumValues.length > enumIndex && values.contains(enumValues[enumIndex])) {
                index++;
            }
            if (enumValues.length > enumIndex) {
                return enumValues[index];
            }
            return null;
        }
        if (clazzType.isInterface()) {
            return Proxy.newProxyInstance(clazzType.getClassLoader(), new Class<?>[] {clazzType}, this);
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return proxy.getClass().toString();
        }
        if (method.getName().equals("hashCode")) {
            return proxy.getClass().toString().hashCode();
        }
        return null;
    }

    /**
     * Потокобезопасный форматтер
     */
    private static class ThreadLocalClassController extends ThreadLocal<Map<Class<?>, Object>> {

        @Override
        public Map<Class<?>, Object> initialValue() {
            return new HashMap<>();
        }

    }
}
package com.reforms.orm.reflex;

import static com.reforms.orm.OrmConfigurator.getInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

/**
 * Собирает информацию об объекте заданного типа и связывает создающий конструктор с полями объекта
 * @author evgenie
 */
class InstanceInformator {

    private final Class<?> clazz;

    private Map<String, Field> fields = new HashMap<>();

    private List<FieldsInfo> fieldsInfoList = new ArrayList<>();

    InstanceInformator(Class<?> clazz, List<InstanceInfo> instancesInfo) {
        this.clazz = clazz;
        scanFields(clazz);
        scanInstancesInfo(instancesInfo);
    }

    FieldsInfo resolveFieldsInfo(Map<String, Object> fieldNames2values) {
        if (fieldsInfoList.size() == 1) {
            return fieldsInfoList.get(0);
        }
        for (FieldsInfo fieldsInfo : fieldsInfoList) {
            boolean findFieldsInfo = true;
            for (FieldInfo fieldInfo : fieldsInfo) {
                if (!fieldNames2values.containsKey(fieldInfo.getFieldName())) {
                    findFieldsInfo = false;
                    break;
                }
            }
            if (findFieldsInfo) {
                return fieldsInfo;
            }
        }
        throw new IllegalStateException("Не найдена информация о конструкторе, с помощью которого можно создать объект для класса '" + clazz + "'");
    }

    private void scanInstancesInfo(List<InstanceInfo> instancesInfo) {
        for (InstanceInfo instanceInfo : instancesInfo) {
            if (instanceInfo.getCause() == null) {
                handleInstance(instanceInfo);
            }
        }
    }

    private void handleInstance(InstanceInfo instanceInfo) {
        FieldsInfo fieldsInfo = resolveFieldsInfo(instanceInfo);
        fieldsInfoList.add(fieldsInfo);
    }

    private FieldsInfo resolveFieldsInfo(InstanceInfo instanceInfo) {
        Constructor<?> constructor = instanceInfo.getConstructor();
        FieldsInfo fieldsInfo = new FieldsInfo(constructor);
        if (constructor.getParameterTypes().length != 0) {
            Object instance1 = instanceInfo.getInstance1();
            Object instance2 = instanceInfo.getInstance2();
            DefaultValueCreator creator = instanceInfo.getCreator();
            DefaultValueArray intialValues1 = creator.getFirstValues();
            DefaultValueArray intialValues2 = creator.getSecondValues();
            for (Entry<String, Field> entryField : fields.entrySet()) {
                Field field = entryField.getValue();
                Object fieldValue1 = getValueFromField(instance1, field);
                Object fieldValue2 = getValueFromField(instance2, field);
                boolean isSame = field.getType().isPrimitive() ? Objects.equals(fieldValue1, fieldValue2) : fieldValue1 == fieldValue2;
                if (!isSame) {
                    int index1 = intialValues1.find(fieldValue1, field.getType());
                    int index2 = intialValues2.find(fieldValue2, field.getType());
                    if (index1 != -1 && index2 != -1) {
                        int resultIndex = Math.max(index1, index2);
                        String fieldName = entryField.getKey();
                        FieldInfo fieldInfo = new FieldInfo(resultIndex, fieldName);
                        fieldsInfo.add(fieldInfo);
                    }
                }
            }
        }
        return fieldsInfo;
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
                    + clazz + "'", roe);
        } finally {
            if (!fieldAccessible) {
                field.setAccessible(fieldAccessible);
            }
        }
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

    public static InstanceInformator createInstanceInformator(Class<?> instanceClass) {
        LocalCache localCache = getInstance(LocalCache.class);
        return localCache.getInstanceInformator(instanceClass);
    }
}
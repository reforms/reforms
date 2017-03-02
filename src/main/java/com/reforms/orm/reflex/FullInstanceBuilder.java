package com.reforms.orm.reflex;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Строит объект с не дефолтным конструктором, передавая только имена полей и их значение
 * TODO: Оптимизация!!
 * @author evgenie
 */
class FullInstanceBuilder implements IInstanceBuilder {

    private Class<?> ormClass;

    private IReflexor reflexor;

    private Map<String, Object> values;

    private FieldsInfo fieldsInfo = null;

    public FullInstanceBuilder(Class<?> ormClass) {
        this.ormClass = ormClass;
        reflexor = Reflexor.createReflexor(ormClass);
    }

    @Override
    public void prepare() {
        values = new LinkedHashMap<>();
    }

    @Override
    public void append(String metaFieldName, Object fieldValue) {
        int dotIndex = metaFieldName.indexOf('.');
        if (dotIndex != -1) {
            String fieldName = metaFieldName.substring(0, dotIndex);
            IInstanceBuilder subBuilder = (IInstanceBuilder) values.get(fieldName);
            if (subBuilder == null) {
                Class<?> subClass = reflexor.getType(fieldName);
                subBuilder = new FullInstanceBuilder(subClass);
                subBuilder.prepare();
                values.put(fieldName, subBuilder);
            }
            String subFieldMeta = metaFieldName.substring(dotIndex + 1);
            subBuilder.append(subFieldMeta, fieldValue);
        } else {
            values.put(metaFieldName, fieldValue);
        }
    }

    @Override
    public Object complete() throws Exception {
        if (fieldsInfo == null) {
            InstanceCreator creator = new InstanceCreator(ormClass);
            InstanceInformator informator = new InstanceInformator(ormClass, creator.processAll());
            fieldsInfo = informator.resolveFieldsInfo(values);
        }
        Object[] constructorValues = new Object[fieldsInfo.size()];
        for (FieldInfo fieldInfo : fieldsInfo) {
            String fieldName = fieldInfo.getFieldName();
            Object fieldValue = values.remove(fieldName);
            if (fieldValue instanceof IInstanceBuilder) {
                fieldValue = ((IInstanceBuilder) fieldValue).complete();
            }
            constructorValues[fieldInfo.getConstructorIndex()] = fieldValue;
        }
        Constructor<?> constructor = fieldsInfo.getConstructor();
        Object instance = constructor.newInstance(constructorValues);
        for (Entry<String, Object> entryField : values.entrySet()) {
            String fieldName = entryField.getKey();
            Object fieldValue = entryField.getValue();
            reflexor.setValue(instance, fieldName, fieldValue);
        }
        return instance;
    }
}
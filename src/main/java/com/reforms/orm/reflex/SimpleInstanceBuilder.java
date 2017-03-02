package com.reforms.orm.reflex;

/**
 * Строит объект с дефолтным конструктором
 * @author evgenie
 */
class SimpleInstanceBuilder implements IInstanceBuilder {

    private Object object;

    private IReflexor reflexor;

    public SimpleInstanceBuilder(IReflexor reflexor) {
        this.reflexor = reflexor;
    }

    @Override
    public void prepare() {
        object = reflexor.createInstance();
    }

    @Override
    public void append(String metaFieldName, Object metaFieldValue) {
        reflexor.setValue(object, metaFieldName, metaFieldValue);
    }

    @Override
    public Object complete() {
        Object result = object;
        object = null;
        return result;
    }
}
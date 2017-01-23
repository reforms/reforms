package com.reforms.orm.reflex;

/**
 * Оставляем возможность быстро спрыгнуть на MethodHandles
 * @author evgenie
 *
 */
public interface IReflexor {

    public Object createInstance();

    public boolean hasKey(String metaFieldName);

    public Object getValue(Object instance, String metaFieldName);

    public Class<?> getType(String metaFieldName);

    public void setValue(Object instance, String metaFieldName, Object value);
}

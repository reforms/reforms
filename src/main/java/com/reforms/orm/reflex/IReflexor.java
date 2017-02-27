package com.reforms.orm.reflex;

import java.lang.reflect.Method;

/**
 * Оставляем возможность быстро спрыгнуть на MethodHandles
 * @author evgenie
 *
 */
public interface IReflexor {

    public Object createInstance();

    public boolean hasKey(String metaFieldName);

    public Object getValue(Object instance, String metaFieldName);

    public Object getValue(IMethodAcceptor methodAcceptor);

    public Method findMethod(IMethodAcceptor methodAcceptor);

    public Object getValue(Object instance, IFieldAcceptor fieldAcceptor);

    public Class<?> getType(String metaFieldName);

    public void setValue(Object instance, String metaFieldName, Object value);
}

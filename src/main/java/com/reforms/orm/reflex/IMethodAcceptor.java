package com.reforms.orm.reflex;

import java.lang.reflect.Method;

/**
 * Контракт на акцепт поля или метода по своим критериям
 * @author evgenie
 */
public interface IMethodAcceptor {

    public boolean acceptMethod(Method method);

    public Object getInstanceObjectFor(Method method);

    public Object[] getArgsFor(Method method);

}

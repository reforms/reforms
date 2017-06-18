package com.reforms.orm.dao.proxy;

import com.reforms.ann.ThreadSafe;

import java.lang.reflect.Method;

/**
 * Дефолтная реализация перехватчика вызовов прокси объектов
 * @author evgenie
 */
@ThreadSafe
public class DefaultMethodInterceptor implements IMethodInterceptor {

    @Override
    public boolean accept(Class<?> interfaze, Object connectionHolder, Object proxy, Method method, Object[] args) throws Exception {
        return false;
    }

    @Override
    public Object invoke(Object connectionHolder, Object proxy, Method method, Object[] args) throws Throwable {
        throw new IllegalStateException("Don't use");
    }
}

package com.reforms.orm.dao.proxy;

import com.reforms.ann.TargetApi;

import java.lang.reflect.Method;

@TargetApi
public interface IMethodInterceptor {

    boolean accept(Class<?> interfaze, Object connectionHolder, Object proxy, Method method, Object[] args) throws Exception;

    Object invoke(Object connectionHolder, Object proxy, Method method, Object[] args) throws Throwable;
}

package com.reforms.orm.reflex;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * API для доступа к приватным метода
 * @author evgenie
 */
public class PrivateAccess implements InvocationHandler {

    private final Object target;

    private PrivateAccess(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = target.getClass();
        Method targetMethod = targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        targetMethod.setAccessible(true);
        return targetMethod.invoke(target, args);
    }

    public static <Interfaze> Interfaze createAccessor(Object target, Class<Interfaze> interfaze) {
        ClassLoader classLoader = interfaze.getClassLoader();
        Class<?>[] interfazeClasses = new Class[]{interfaze};
        InvocationHandler handler = new PrivateAccess(target);
        return (Interfaze) Proxy.newProxyInstance(classLoader, interfazeClasses, handler);
    }
}
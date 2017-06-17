package com.reforms.orm.reflex;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Зеркало
 * @author evgenie
 */
public class Mirror implements InvocationHandler {

    private int index = 0;
    private final Object[] targets;

    private Mirror(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return String.valueOf("" + index + ": " + targets[index]);
        }
        return targets[index++];
    }

    public static <Interfaze> Interfaze of(Object[] targets, Class<Interfaze> interfaze) {
        ClassLoader classLoader = interfaze.getClassLoader();
        Class<?>[] interfazeClasses = new Class[]{interfaze};
        InvocationHandler handler = new Mirror(targets);
        return (Interfaze) Proxy.newProxyInstance(classLoader, interfazeClasses, handler);
    }
}
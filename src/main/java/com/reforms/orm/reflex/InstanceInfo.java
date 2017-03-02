package com.reforms.orm.reflex;

import java.lang.reflect.Constructor;

/**
 *
 * @author evgenie
 */
public class InstanceInfo {

    private Constructor<?> constructor;

    private Object instance1;

    private Object instance2;

    private DefaultValueCreator creator;

    private Throwable cause;

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Object getInstance1() {
        return instance1;
    }

    public void setInstance1(Object instance1) {
        this.instance1 = instance1;
    }

    public Object getInstance2() {
        return instance2;
    }

    public void setInstance2(Object instance2) {
        this.instance2 = instance2;
    }

    public DefaultValueCreator getCreator() {
        return creator;
    }

    public void setCreator(DefaultValueCreator creator) {
        this.creator = creator;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}

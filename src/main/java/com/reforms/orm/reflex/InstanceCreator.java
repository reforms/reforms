package com.reforms.orm.reflex;

import com.reforms.ann.TargetConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Создать объект с не дефолтным конструктором.
 * TODO: добавить тест на конструктор копирования и рефакторинг
 * @author evgenie
 */
public class InstanceCreator {

    public static final Object CANT_CREATE_INSTANCE_MARKER = new Object();

    private Class<?> ormClass;

    private boolean simpleConstructorFlag;

    public InstanceCreator(Class<?> ormClass) {
        this.ormClass = ormClass;
    }

    public Class<?> getOrmClass() {
        return ormClass;
    }

    public InstanceInfo processSingle() {
        Constructor<?>[] allConstructors = ormClass.getDeclaredConstructors();
        List<Constructor<?>> constructors = filterConstructors(allConstructors);
        for (Constructor<?> constructor : constructors) {
            InstanceInfo instanceInfo = new InstanceInfo();
            Object instance1 = CANT_CREATE_INSTANCE_MARKER;
            Object instance2 = CANT_CREATE_INSTANCE_MARKER;
            Throwable cantCreateCause = null;
            DefaultValueCreator defaultCreator = new DefaultValueCreator();
            try {
                instance1 = createFirstInstance(constructor, defaultCreator);
                if (constructor.getParameterTypes().length == 0) {
                    simpleConstructorFlag = true;
                } else {
                    instance2 = createSecondInstance(constructor, defaultCreator);
                }
            } catch (Throwable cause) {
                checkException(cause);
                cantCreateCause = cause;
            }
            if (cantCreateCause == null) {
                instanceInfo.setConstructor(constructor);
                instanceInfo.setInstance1(instance1);
                instanceInfo.setInstance2(instance2);
                instanceInfo.setCreator(defaultCreator);
                instanceInfo.setCause(cantCreateCause);
                return instanceInfo;
            }
        }
        throw new IllegalStateException("Не возможно создать экземпляр класса '" + ormClass + "'");
    }

    public List<InstanceInfo> processAll() {
        List<InstanceInfo> instancesInfo = new ArrayList<>();
        Constructor<?>[] allConstructors = ormClass.getDeclaredConstructors();
        List<Constructor<?>> constructors = filterConstructors(allConstructors);
        for (Constructor<?> constructor : constructors) {
            InstanceInfo instanceInfo = new InstanceInfo();
            Object instance1 = CANT_CREATE_INSTANCE_MARKER;
            Object instance2 = CANT_CREATE_INSTANCE_MARKER;
            Throwable cantCreateCause = null;
            DefaultValueCreator defaultCreator = new DefaultValueCreator();
            try {
                instance1 = createFirstInstance(constructor, defaultCreator);
                if (constructor.getParameterTypes().length == 0) {
                    simpleConstructorFlag = true;
                } else {
                    instance2 = createSecondInstance(constructor, defaultCreator);
                }
            } catch (Throwable cause) {
                checkException(cause);
                cantCreateCause = cause;
            }
            instanceInfo.setConstructor(constructor);
            instanceInfo.setInstance1(instance1);
            instanceInfo.setInstance2(instance2);
            instanceInfo.setCreator(defaultCreator);
            instanceInfo.setCause(cantCreateCause);
            instancesInfo.add(instanceInfo);

        }
        return instancesInfo;
    }

    private void checkException(Throwable cause) {
        if (cause instanceof IllegalStateException) {
            throw (IllegalStateException) cause;
        }
    }

    public boolean isSimpleConstructorFlag() {
        return simpleConstructorFlag;
    }

    private List<Constructor<?>> filterConstructors(Constructor<?>[] constructors) {
        boolean hasTargetConstructor = false;
        List<Constructor<?>> targets = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            boolean acceptConstructor = true;
            Class<?>[] paramTypes = constructor.getParameterTypes();
            for (Class<?> paramType : paramTypes) {
                if (ormClass.isAssignableFrom(paramType) || paramType == ormClass) {
                    acceptConstructor = false;
                    break;
                }
            }
            if (acceptConstructor) {
                targets.add(constructor);
                hasTargetConstructor |= constructor.isAnnotationPresent(TargetConstructor.class);
            }
        }
        if (!hasTargetConstructor) {
            return targets;
        }
        Iterator<Constructor<?>> iterator = targets.iterator();
        while (iterator.hasNext()) {
            Constructor<?> constructor = iterator.next();
            if (!constructor.isAnnotationPresent(TargetConstructor.class)) {
                iterator.remove();
            }
        }
        return targets;
    }

    private Object createFirstInstance(Constructor<?> constructor, DefaultValueCreator defaultCreator) throws Throwable {
        int modifiers = constructor.getModifiers();
        boolean notAccessible = !Modifier.isPublic(modifiers);
        if (notAccessible) {
            constructor.setAccessible(true);
        }
        try {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            for (Class<?> paramType : paramTypes) {
                defaultCreator.createFirst(paramType);
            }
            return constructor.newInstance(defaultCreator.getFirstValues().toArray());
        } finally {
            if (notAccessible) {
                constructor.setAccessible(false);
            }
        }
    }

    private Object createSecondInstance(Constructor<?> constructor, DefaultValueCreator defaultCreator) throws Throwable {
        int modifiers = constructor.getModifiers();
        boolean notAccessible = !Modifier.isPublic(modifiers);
        if (notAccessible) {
            constructor.setAccessible(true);
        }
        try {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            for (Class<?> paramType : paramTypes) {
                defaultCreator.createSecond(paramType);
            }
            return constructor.newInstance(defaultCreator.getSecondValues().toArray());
        } finally {
            if (notAccessible) {
                constructor.setAccessible(false);
            }
        }
    }
}
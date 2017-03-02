package com.reforms.orm.reflex;

import com.reforms.ann.TargetConstructor;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.OrmContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Создать объект с не дефолтным конструктором.
 * TODO: добавить тест на конструктор копирования и рефакторинг
 * @author evgenie
 */
class InstanceCreator {

    public static final Object STUB_INSTANCE_MARKER = new Object();

    private Class<?> ormClass;

    private boolean simpleConstructorFlag;

    private List<InstanceInfo> instancesInfo;

    InstanceCreator(Class<?> ormClass) {
        this.ormClass = ormClass;
        instancesInfo = Collections.unmodifiableList(processAll());
    }

    public Class<?> getOrmClass() {
        return ormClass;
    }

    public List<InstanceInfo> getInstancesInfo() {
        return instancesInfo;
    }

    public boolean isSimpleConstructorFlag() {
        return simpleConstructorFlag;
    }

    public InstanceInfo getFirstInstanceInfo() {
        if (instancesInfo.size() == 0) {
            throw new IllegalStateException("Не возможно создать экземпляр класса '" + ormClass + "'");
        }
        return instancesInfo.get(0);
    }

    private List<InstanceInfo> processAll() {
        List<InstanceInfo> instancesInfo = new ArrayList<>();
        Constructor<?>[] allConstructors = ormClass.getDeclaredConstructors();
        List<Constructor<?>> constructors = filterConstructors(allConstructors);
        for (Constructor<?> constructor : constructors) {
            InstanceInfo instanceInfo = new InstanceInfo();
            Object instance1 = STUB_INSTANCE_MARKER;
            Object instance2 = STUB_INSTANCE_MARKER;
            Throwable cantCreateCause = null;
            DefaultValueCreator defaultCreator = new DefaultValueCreator();
            try {
                instance1 = createFirstInstance(constructor, defaultCreator);
                if (constructor.getParameterTypes().length == 0) {
                    simpleConstructorFlag = true;
                    instance1 = STUB_INSTANCE_MARKER;
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

    public static InstanceCreator createInstanceCreator(Class<?> instanceClass) {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        LocalCache localCache = rCtx.getLocalCache();
        return localCache.getInstanceCreator(instanceClass);
    }
}
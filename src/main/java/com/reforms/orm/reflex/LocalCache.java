package com.reforms.orm.reflex;

import java.util.concurrent.ConcurrentHashMap;

import com.reforms.ann.ThreadSafe;

/**
 * Кэшируем экземпляры Reflexor и внутри их - кешируем найденные методы и объекты
 * @author evgenie
 *
 */
@ThreadSafe
public class LocalCache {

    private ConcurrentHashMap<Class<?>, IReflexor> reflexors = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<?>, IEnumReflexor> enumReflexors = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<?>, InstanceCreator> creators = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<?>, InstanceInformator> informators = new ConcurrentHashMap<>();

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz класс
     * @return рефлексор
     */
    public IReflexor getReflexor(Class<?> clazz) {
        IReflexor reflexor = reflexors.get(clazz);
        if (reflexor == null) {
            reflexor = new Reflexor(clazz);
            IReflexor oldReflexor = reflexors.putIfAbsent(clazz, reflexor);
            if (oldReflexor != null) {
                reflexor = oldReflexor;
            }
        }
        return reflexor;
    }

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz класс
     * @return рефлексор
     */
    public IEnumReflexor getEnumReflexor(Class<?> clazz) {
        IEnumReflexor reflexor = enumReflexors.get(clazz);
        if (reflexor == null) {
            reflexor = new EnumReflexor(clazz);
            IEnumReflexor oldReflexor = enumReflexors.putIfAbsent(clazz, reflexor);
            if (oldReflexor != null) {
                reflexor = oldReflexor;
            }
        }
        return reflexor;
    }

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz класс
     * @return создатель объектов
     */
    public InstanceCreator getInstanceCreator(Class<?> clazz) {
        InstanceCreator creator = creators.get(clazz);
        if (creator == null) {
            creator = new InstanceCreator(clazz).init();
            InstanceCreator oldCreator = creators.putIfAbsent(clazz, creator);
            if (oldCreator != null) {
                creator = oldCreator;
            }
        }
        return creator;
    }

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz класс
     * @return информатор
     */
    public InstanceInformator getInstanceInformator(Class<?> clazz) {
        InstanceInformator informator = informators.get(clazz);
        if (informator == null) {
            InstanceCreator creator = getInstanceCreator(clazz);
            informator = new InstanceInformator(clazz, creator.getInstancesInfo());
            InstanceInformator oldInformator = informators.putIfAbsent(clazz, informator);
            if (oldInformator != null) {
                informator = oldInformator;
            }
        }
        return informator;
    }
}
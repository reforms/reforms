package com.reforms.orm.reflex;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Кэшируем экземпляры Reflexor и внутри их - кешируем найденные методы и объекты
 * @author evgenie
 *
 */
public class LocalCache {

    private ConcurrentHashMap<Class<?>, IReflexor> reflexors = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<?>, IEnumReflexor> enumReflexors = new ConcurrentHashMap<>();

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz
     * @return
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
     * @param clazz
     * @return
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
}
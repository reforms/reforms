package com.reforms.orm.reflex;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Кэшируем экземпляры Reflexor и внутри их - кешируем найденные методы и объекты
 * @author evgenie
 *
 */
public class ReflexorCache {

    private ConcurrentHashMap<Class<?>, IReflexor> reflexors = new ConcurrentHashMap<>();

    /**
     * Умышленно не делаем грамотную синхронизацию
     * @param clazz
     * @return
     */
    public IReflexor get(Class<?> clazz) {
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
}
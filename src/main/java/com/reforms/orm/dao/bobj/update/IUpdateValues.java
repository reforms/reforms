package com.reforms.orm.dao.bobj.update;

import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.IValues;

/**
 * Именованный контракт на обновление
 * @author evgenie
 */
public abstract class IUpdateValues implements IValues, IPriorityValues {

    /**
     * Получить значение именнованного параметра
     * @param key ключ доступа к значению именнованного параметра
     */
    @Override
    public Object getPriorityValue(int priority, String key) {
        return get(key);
    }

    /**
     * Получить значение параметра по индексу
     * @param key - порядковый номер значения
     */
    @Override
    public Object getPriorityValue(int priority, int key) {
        return get(key);
    }
}

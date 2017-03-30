package com.reforms.orm.dao.filter;

import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.IValues;
import com.reforms.orm.dao.paging.IPageFilter;

/**
 * Контракт на получение значения фильтра по ключу
 * @author evgenie
 */
public abstract class IFilterValues implements IValues, IPriorityValues {

    /**
     * Получить фильтр постраничной загрузки
     * @return фильтр постраничной загрузки
     */
    public abstract IPageFilter getPageFilter();

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

    @Override
    public int getParamNameType(int priority) {
        return getParamNameType();
    }
}

package com.reforms.orm.dao.filter.param;

import com.reforms.orm.dao.filter.page.IPageFilter;

/**
 * Контракт на получение значения фильтра по ключу
 * @author evgenie
 */
public interface IFilterValues extends IPageFilter {

    /**
     * Получить значение именнованного параметр фильтрации для запроса
     * @param key ключ доступа к значению фильтра
     */
    public Object get(String key);

    /**
     * Получить значение параметра фильтрации по индексу(порядковый номер в запросе)
     * @param key - порядковый номер значения фильтра
     */
    public Object get(int key);

    /**
     * Применить фильтр постраничной загрузки данных
     * @param newPageFiler новый фильтр
     */
    public void applyPageFilter(IPageFilter newPageFiler);

}

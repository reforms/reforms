package com.reforms.orm.filter;

/**
 * Контракт на получение значения фильтра по ключу
 * @author evgenie
 */
public interface FilterValues extends PageFilter {

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

}

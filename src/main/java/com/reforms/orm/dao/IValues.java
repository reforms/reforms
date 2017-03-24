package com.reforms.orm.dao;

/**
 * Контракт на получение значения по ключу и индексу
 * @author evgenie
 */
public interface IValues extends IParamNameType {

    /**
     * Получить значение именнованного параметра
     * @param key ключ доступа к значению именнованного параметра
     */
    public Object get(String key);

    /**
     * Получить значение параметра по индексу
     * @param key - порядковый номер значения
     */
    public Object get(int key);

    /**
     * Проверить наличие данных
     * @return true - данные есть, false - нет
     */
    public boolean isEmpty();
}

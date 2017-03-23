package com.reforms.orm.dao;

/**
 * Получение значений по приоритету. Имеет огромное значение при комбинировании объектов типа IValues
 * @author evgenie
 */
public interface IPriorityValues {

    /** Вставка */
    public static final int PV_INSERT = 0;

    /** Обновление */
    public static final int PV_UPDATE = 1;

    /** Удаление */
    public static final int PV_DELETE = 2;

    /** Фильтрация */
    public static final int PV_FILTER = 3;

    /**
     * Получить значение именнованного параметра
     * @param key ключ доступа к значению именнованного параметра
     */
    public Object getPriorityValue(int priority, String key);

    /**
     * Получить значение параметра по индексу
     * @param key - порядковый номер значения
     */
    public Object getPriorityValue(int priority, int key);
}
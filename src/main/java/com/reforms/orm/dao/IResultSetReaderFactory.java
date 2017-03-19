package com.reforms.orm.dao;

import java.util.List;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на получение объекта IResultSetReader
 * @author evgenie
 */
public interface IResultSetReaderFactory {

    /**
     * Найти подходящий ридер по классу и списку колонок
     * @param ormClass класс объекта, который необходимо считать из ResultSet
     * @param columns список колонок
     * @return считыватель объектов или NULL, если не удалось найти подходящий объект
     */
    public IResultSetObjectReader resolveReader(Class<?> ormClass, List<SelectedColumn> columns);
}

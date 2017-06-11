package com.reforms.orm.dao.filter.column;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Фильтр на ограничение выбираемых колонок
 * @author evgenie
 */
@FunctionalInterface
public interface ISelectedColumnFilter {

    /**
     * Проверить, необходимо ли выбирать колонку указанного типа
     * @param selectedColumn конкретная колонка
     * @return FS_ACCEPT     - участвует и в выборке и в sql-выражении,
     *         FS_NOT_ACCEPT - не участвует в выборке, но есть в sql-выражении
     *         FS_REMOVE     - не участвует ни в выборке, ни в sql-выражении
     */
    public FilterState acceptSelectedColumn(SelectedColumn selectedColumn);
}
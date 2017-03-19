package com.reforms.orm.selectable;

import com.reforms.orm.select.SelectedColumn;

/**
 * Фильтр на ограничение выбираемых колонок
 * @author evgenie
 */
public interface ISelectedColumnFilter {

    /**
     * Проверить, необходимо ли выбирать колонку указанного типа
     * @param selectedColumn конкретная колонка
     * @return true - выбирать необходимо, false -  иначе
     */
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn);
}
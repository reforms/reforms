package com.reforms.orm.select.report.converter;

import java.sql.ResultSet;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * TODO refactoring: привести к одному стилю или везде добавить I к интервейсам или везде убрать
 * @author evgenie
 */
public interface IColumnValueConverter {

    public String convertValue(SelectedColumn column, ResultSet rs) throws Exception;

}

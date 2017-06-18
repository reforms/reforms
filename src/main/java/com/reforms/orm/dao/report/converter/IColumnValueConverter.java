package com.reforms.orm.dao.report.converter;

import com.reforms.ann.TargetApi;
import com.reforms.orm.dao.column.SelectedColumn;

import java.sql.ResultSet;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * TODO refactoring: привести к одному стилю или везде добавить I к интервейсам или везде убрать
 * @author evgenie
 */
@FunctionalInterface
@TargetApi
public interface IColumnValueConverter {

    public String convertValue(SelectedColumn column, ResultSet rs) throws Exception;

}

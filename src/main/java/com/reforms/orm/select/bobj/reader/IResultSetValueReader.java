package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения <ParamType> из выборки ResultSet
 * @author evgenie
 */
public interface IResultSetValueReader<ParamType> {

    public ParamType readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws Exception;

}

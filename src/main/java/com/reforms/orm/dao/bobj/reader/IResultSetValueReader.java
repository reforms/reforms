package com.reforms.orm.dao.bobj.reader;

import com.reforms.ann.TargetApi;
import com.reforms.orm.dao.column.SelectedColumn;

import java.sql.ResultSet;

/**
 * Контракт на чтение значения <ParamType> из выборки ResultSet
 * @author evgenie
 */
@FunctionalInterface
@TargetApi
public interface IResultSetValueReader<ParamType> {

    public ParamType readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws Exception;

}

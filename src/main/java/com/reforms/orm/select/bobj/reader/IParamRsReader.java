package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;

/**
 * Контракт на чтение значения <ParamType> из выборки ResultSet
 * @author evgenie
 */
public interface IParamRsReader<ParamType> {

    public ParamType readValue(int columnIndex, ResultSet rs) throws Exception;

}

package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.ResultSetOrmReader;
import com.reforms.orm.dao.bobj.ResultSetSingleReader;
import com.reforms.orm.dao.bobj.reader.IResultSetValueReader;
import com.reforms.orm.dao.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.report.ResultSetRecordReader;
import com.reforms.orm.dao.report.model.ReportRecord;

import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.reflex.ClassUtils.isEnumClass;

/**
 * Фабрика для получения IResultSetReader объектов
 * @author evgenie
 */
public class ResultSetReaderFactory implements IResultSetReaderFactory {

    @Override
    public IResultSetObjectReader resolveReader(Class<?> objClass, List<SelectedColumn> columns) {
        if (ReportRecord.class == objClass) {
            return new ResultSetRecordReader(columns);
        }
        ResultSetValueReaderFactory resultSetValueReaderFactory = getInstance(ResultSetValueReaderFactory.class);
        Object key = objClass;
        if (isEnumClass(objClass)) {
            key = Enum.class;
        }
        IResultSetValueReader<?> singleParamReader = resultSetValueReaderFactory.getParamRsReader(key);
        if (singleParamReader != null) {
            return new ResultSetSingleReader(objClass, columns);
        }
        return new ResultSetOrmReader(objClass, columns);
    }
}
package com.reforms.orm.select;

import com.reforms.orm.select.bobj.ResultSetOrmReader;
import com.reforms.orm.select.bobj.ResultSetSingleReader;
import com.reforms.orm.select.bobj.reader.IResultSetValueReader;
import com.reforms.orm.select.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.select.report.ResultSetRecordReader;
import com.reforms.orm.select.report.model.ReportRecord;

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
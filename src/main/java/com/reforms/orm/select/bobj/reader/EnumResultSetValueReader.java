package com.reforms.orm.select.bobj.reader;

import com.reforms.orm.reflex.IEnumReflexor;
import com.reforms.orm.select.SelectedColumn;

import java.sql.ResultSet;

import static com.reforms.orm.reflex.EnumReflexor.createEnumReflexor;

/**
 * Контракт на чтение значения Enum из выборки ResultSet
 * @author evgenie
 */
class EnumResultSetValueReader implements IResultSetValueReader<Enum<?>> {

    private ResultSetValueReaderFactory factory;

    public EnumResultSetValueReader(ResultSetValueReaderFactory factory) {
        this.factory = factory;
    }

    @Override
    public Enum<?> readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(toBeClass);
        Class<?> assignToBeReadClass = enumReflexor.getAssignValueClass();
        IResultSetValueReader<?> realReader = factory.getParamRsReader(assignToBeReadClass);
        if (realReader == null) {
            throw new IllegalStateException("Не найден IParamRsReader для чтения из ResultSet значения для класса '" + assignToBeReadClass + "'");
        }
        Object assignValue = realReader.readValue(column, rs, assignToBeReadClass);
        return (Enum<?>) enumReflexor.getEnumValue(assignValue);
    }

}

package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр произвольного типа
 * @author evgenie
 */
@ThreadSafe
public class ObjectParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Object objectValue = convertValue(value);
        ps.setObject(index, objectValue);
    }

    @Override
    public boolean acceptValue(Object value) {
        return true;
    }

    protected Object convertValue(Object value) {
        return value;
    }
}
package com.reforms.orm.dao.filter.param;

import com.reforms.ann.TargetApi;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Контракт на установку параметра в ResultSet
 * @author evgenie
 */
@TargetApi
public interface ParamSetter {

    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException;

    public boolean acceptValue(Object value);

}

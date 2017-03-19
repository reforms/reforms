package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Контракт на установку параметра в ResultSet
 * @author evgenie
 */
public interface ParamSetter {

    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException;

    public boolean acceptValue(Object value);

}

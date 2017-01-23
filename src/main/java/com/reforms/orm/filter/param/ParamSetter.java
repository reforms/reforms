package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParamSetter {

    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException;

}

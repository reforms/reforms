package com.reforms.orm.dao.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.orm.dao.IPriorityValues;

/**
 * Контракт на воспроизведение совершенных действий
 * @author evgenie
 */
public interface IBatcher {

    void add(IPriorityValues values, PreparedStatement ps) throws SQLException;
}

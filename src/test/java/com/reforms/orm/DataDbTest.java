package com.reforms.orm;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataDbTest {

    protected H2DataSource h2ds;

    protected DataDbTest(String dbName) {
        h2ds = new H2DataSource(dbName);
    }

    public void invokeStatement(String query) {
        try (PreparedStatement ps = h2ds.getConnection().prepareStatement(query)) {
            ps.execute();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Невозможно выполнить запрос '" + query + "'", sqle);
        }
    }

    public void close() {
        h2ds.close();
    }
}

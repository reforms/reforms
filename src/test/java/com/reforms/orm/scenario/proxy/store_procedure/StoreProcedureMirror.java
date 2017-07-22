package com.reforms.orm.scenario.proxy.store_procedure;

import org.h2.tools.SimpleResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Хранимки для h2
 * @author evgenie
 */
public class StoreProcedureMirror {

    private static int id = 0;

    public static int getNextId() {
        return id++;
    }

    public static long getObject(String name) {
        return "1".equals(name) ? 1L : 0L;
    }

    public static ResultSet loadClient(Connection conn) throws SQLException {
        SimpleResultSet clientRs = new SimpleResultSet();
        clientRs.addColumn("id", Types.INTEGER, 10, 0);
        clientRs.addColumn("name", Types.VARCHAR, 255, 0);
        String url = conn.getMetaData().getURL();
        if (url.equals("jdbc:columnlist:connection")) {
            return clientRs;
        }
        clientRs.addRow(1, "first client");
        return clientRs;
    }

    public static ResultSet loadClients(Connection conn) throws SQLException {
        SimpleResultSet clientRs = new SimpleResultSet();
        clientRs.addColumn("id", Types.INTEGER, 10, 0);
        clientRs.addColumn("name", Types.VARCHAR, 255, 0);
        String url = conn.getMetaData().getURL();
        if (url.equals("jdbc:columnlist:connection")) {
            return clientRs;
        }
        clientRs.addRow(1, "first client");
        clientRs.addRow(2, "second client");
        return clientRs;
    }
}

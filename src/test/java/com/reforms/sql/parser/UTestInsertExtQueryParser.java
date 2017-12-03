package com.reforms.sql.parser;

import com.reforms.sql.expr.query.InsertQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author evgenie
 * TODO есть кейсы sql запросов:
 * INSERT INTO users (email, appName, region, name, pass, last_modified, last_logged, last_logged_ip, is_facebook_user, is_super_admin, energy, json)
 *  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
 *      ON CONFLICT (email, appName)
 *          DO UPDATE SET pass = EXCLUDED.pass, name = EXCLUDED.name, last_modified = EXCLUDED.last_modified, last_logged = EXCLUDED.last_logged, last_logged_ip = EXCLUDED.last_logged_ip, is_facebook_user = EXCLUDED.is_facebook_user, is_super_admin = EXCLUDED.is_super_admin, energy = EXCLUDED.energy, json = EXCLUDED.json, region = EXCLUDED.region
 */
public class UTestInsertExtQueryParser {

    @Test
    public void testInsertQueryParsing() {
        assertInsertQuery("INSERT INTO tableName (c1 AS :i#, c2 AS :t#)");

        assertInsertQuery("INSERT INTO tableName (c1 AS 1, c2 AS 2)");

        assertInsertQuery("INSERT INTO tableName (c1 AS NOW(), c2 AS (SELECT 1))");
    }

    private void assertInsertQuery(String query) {
        assertInsertQuery(query, null);
    }

    private void assertInsertQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        InsertQuery insertQuery = sqlParser.parseInsertQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, insertQuery);
    }

    private void assertQuery(String query, InsertQuery insertQuery) {
        assertEquals(query, queryToString(insertQuery));
    }

    private String queryToString(InsertQuery insertQuery) {
        SqlBuilder builder = new SqlBuilder();
        insertQuery.view(builder);
        return builder.getQuery();
    }
}
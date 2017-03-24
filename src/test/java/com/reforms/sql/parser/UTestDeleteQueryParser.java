package com.reforms.sql.parser;

import com.reforms.sql.expr.query.DeleteQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author evgenie
 */
public class UTestDeleteQueryParser {

    @Test
    public void testUpdateStatementParsing() {
        assertDeleteQuery("DELETE FROM tableName");
        assertDeleteQuery("DELETE FROM schemeName.tableName");
        assertDeleteQuery("DELETE FROM \"tableName\"");
        assertDeleteQuery("DELETE FROM \"schemeName\".\"tableName\"");
        assertDeleteQuery("DELETE FROM \"schemeName\".[tableName]");
        assertDeleteQuery("DELETE FROM [schemeName].[tableName]");
        //assertDeleteQuery("DELETE aliasName FROM tableName AS aliasName");
    }

    @Test
    public void testWhereStatement() {
        assertConditionStatement("WHERE t1.id = t2.id");
        assertConditionStatement("WHERE t1.id <> t2.id");
        assertConditionStatement("WHERE t1.id < t2.id");
        assertConditionStatement("WHERE t1.id > t2.id");
        assertConditionStatement("WHERE t1.id >= t2.id");
        assertConditionStatement("WHERE t1.id <= t2.id");
        assertConditionStatement("WHERE t1.id <= ?");
        assertConditionStatement("WHERE t1.id != ?");
        assertConditionStatement("WHERE t1.id = TRUE");
        assertConditionStatement("WHERE t1.id = FALSE");
        assertConditionStatement("WHERE t1.id + 2 = t2.id + 7");
        assertConditionStatement("WHERE t1.id - 2 = t2.id - 7");
        assertConditionStatement("WHERE t1.id / 2 = t2.id / 7");
        assertConditionStatement("WHERE t1.id * 2 = t2.id * 7");
        assertConditionStatement("WHERE t1.id * (t.x + t.y) = t2.id * 7");
        assertConditionStatement("WHERE t1.id * (t.x || t.y) = t2.id * 7");
        assertConditionStatement("WHERE t1.id = t2.id AND t1.name = t2.name");
        assertConditionStatement("WHERE (t1.id = t2.id AND t1.name = t2.name)");
        assertConditionStatement("WHERE (t1.id = t2.id AND t1.name = t2.name) OR t1.val <> t2.val");
        assertConditionStatement("WHERE t1.id = (SELECT 1)");
        assertConditionStatement("WHERE (SELECT 1) = t1.id");
        assertConditionStatement("WHERE ((SELECT 1) = t1.id OR t2.id = (SELECT 2))");
        // IS NULL/IS NOT NULL
        assertConditionStatement("WHERE t1.id IS NOT NULL");
        assertConditionStatement("WHERE t1.id IS NULL");
        assertConditionStatement("WHERE (SELECT 1) IS NOT NULL");
        assertConditionStatement("WHERE (SELECT 1) IS NULL");
        // EXISTS
        assertConditionStatement("WHERE NOT EXISTS (SELECT 1 FROM schemeName.test_clients)");
        assertConditionStatement("WHERE EXISTS (SELECT 1 FROM schemeName.test_clients)");
        // BETWEEN
        assertConditionStatement("WHERE t1.value BETWEEN t1.value1 AND t2.value2");
        assertConditionStatement("WHERE t1.value NOT BETWEEN t1.value1 AND t2.value2");
        // NOT
        assertConditionStatement("WHERE t1.column4 = TRUE OR NOT t2.column4 OR t2.column5");
        assertConditionStatement("WHERE t1.column4 = TRUE OR (NOT t2.column4) OR t2.column5");
        assertConditionStatement("WHERE NOT ((client_id, name_cln, addr_cln) = (60, 'архивный юрик', 'moskow') OR client_id = 60)");
        // IN
        assertConditionStatement("WHERE t1 IN (SELECT id FROM tableName3 t3)");
        assertConditionStatement("WHERE t1 IN (?, ?, ?)");
        assertConditionStatement("WHERE t1 IN (?, t2.column3, ?)");
        assertConditionStatement("WHERE t1 IN (1)");
        assertConditionStatement("WHERE t1 IN (1, 2)");
        assertConditionStatement("WHERE t1 IN ('1', '2')");
        // LIKE
        assertConditionStatement("WHERE UPPER(td.text_field) LIKE ? ESCAPE '#'");
        assertConditionStatement("WHERE LOWER(t2.name_cln) LIKE ?");
        assertConditionStatement("WHERE LOWER(t2.name_cln) LIKE '12' || '_13' ESCAPE '_'");
        assertConditionStatement("WHERE LOWER(t2.name_cln) LIKE ('12' || '_13') ESCAPE '_'");
        assertConditionStatement("WHERE UPPER(t2.name_cln) LIKE ('12' || '_13') ESCAPE ('' || '_')");
        // QUANTIFIER
        assertConditionStatement("WHERE 2 = ANY (SELECT client_id FROM schemeName.test_clients cl)");
        assertConditionStatement("WHERE 2 = ALL (SELECT client_id FROM schemeName.test_clients cl)");
        assertConditionStatement("WHERE 2 = SOME (SELECT client_id FROM schemeName.test_clients cl)");
        // UNIQUE
        assertConditionStatement("WHERE UNIQUE (SELECT client_id FROM schemeName.test_clients)");
        assertConditionStatement("WHERE (UNIQUE (SELECT client_id FROM schemeName.test_clients))");
        // assertWhereStatement("WHERE (1) MATCH (SELECT client_id FROM schemeName.test_clients)");
        // CASE
        assertConditionStatement("WHERE name LIKE '12' OR (CASE WHEN id = ? OR id = ? THEN 0 ELSE id END) > 2");
    }

    private void assertConditionStatement(String whereStatement) {
        assertDeleteQuery("DELETE FROM schemeKey.tableName " + whereStatement);
    }

    private void assertDeleteQuery(String query) {
        assertDeleteQuery(query, null);
    }

    private void assertDeleteQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        DeleteQuery deleteQuery = sqlParser.parseDeleteQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, deleteQuery);
    }

    private void assertQuery(String query, DeleteQuery deleteQuery) {
        assertEquals(query, queryToString(deleteQuery));
    }

    private String queryToString(DeleteQuery deleteQuery) {
        SqlBuilder builder = new SqlBuilder();
        deleteQuery.view(builder);
        return builder.getQuery();
    }
}
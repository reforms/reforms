package com.reforms.sql.parser;

import com.reforms.sql.expr.query.UpdateQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author evgenie
 */
public class UTestUpdateQueryParser {

    @Test
    public void testUpdateStatementParsing() {
        assertUpdateStatement("UPDATE tableName");
        assertUpdateStatement("UPDATE schemeName.tableName");
        assertUpdateStatement("UPDATE \"tableName\"");
        assertUpdateStatement("UPDATE \"schemeName\".\"tableName\"");
        assertUpdateStatement("UPDATE \"schemeName\".[tableName]");
        assertUpdateStatement("UPDATE [schemeName].[tableName]");
    }

    private void assertUpdateStatement(String query) {
        String newQuery = query + " SET c1 = ?";
        assertUpdateQuery(newQuery);
    }

    @Test
    public void testSetClasueStatementParsing() {
        assertSetClasueStatement("SET c1 = ?");
        assertSetClasueStatement("SET c1 = ?, c2 = ?");
        assertSetClasueStatement("SET c1 = ?, c2 = ?, c3 = ?");
        assertSetClasueStatement("SET c1 = 'A', c2 = 12, c3 = NULL, c4 = DEFAULT, c5 = TRUE, c6 = FALSE");
        assertSetClasueStatement("SET c1 = INTERVAL '1 day', c2 = TIMESTAMP '2017-01-01 10:23:54'");
        assertSetClasueStatement("SET c1 = DATE '2017-01-01', c2 = TIME '10:23:54'");
        assertSetClasueStatement("SET c1 = c1 + 1");
        assertSetClasueStatement("SET c1 = (c1 + 1)");
        assertSetClasueStatement("SET c1 = (c1 + 1) * 7");
        assertSetClasueStatement("SET c1 = ((c1 + 1) * 7)");
        assertSetClasueStatement("SET c1 = c1 + INTERVAL '1 day'");
        assertSetClasueStatement("SET c1 = (c1 + INTERVAL '1 day')::DATE");
        assertSetClasueStatement("SET c1 = ((c1 + INTERVAL '1 day')::DATE)");
    }

    @Test
    public void testSetClasueStatementParsingWithFilter() {
        assertSetClasueStatement("SET c1 = :fieldName");
        assertSetClasueStatement("SET c1 = ::fieldName");
        assertSetClasueStatement("SET c1 = :bobj.fieldName");
        assertSetClasueStatement("SET c1 = ::bobj.fieldName");
        assertSetClasueStatement("SET c1 = :bobj1.bobj2.fieldName");
        assertSetClasueStatement("SET c1 = ::bobj1.bobj2.fieldName");
        assertSetClasueStatement("SET c1 = :t#");
        assertSetClasueStatement("SET c1 = ::t#");
        assertSetClasueStatement("SET c1 = :t#fieldName");
        assertSetClasueStatement("SET c1 = ::t#fieldName");
    }

    private void assertSetClasueStatement(String query) {
        String newQuery = "UPDATE t1 " + query;
        assertUpdateQuery(newQuery);
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
        assertUpdateQuery("UPDATE schemeKey.tableName SET columnName = ? " + whereStatement);
    }

    private void assertUpdateQuery(String query) {
        assertUpdateQuery(query, null);
    }

    private void assertUpdateQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        UpdateQuery updateQuery = sqlParser.parseUpdateQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, updateQuery);
    }

    private void assertQuery(String query, UpdateQuery updateQuery) {
        assertEquals(query, queryToString(updateQuery));
    }

    private String queryToString(UpdateQuery updateQuery) {
        SqlBuilder builder = new SqlBuilder();
        updateQuery.view(builder);
        return builder.getQuery();
    }
}
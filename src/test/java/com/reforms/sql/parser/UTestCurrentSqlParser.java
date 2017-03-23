package com.reforms.sql.parser;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author evgenie
 */
public class UTestCurrentSqlParser {

    @Test
    public void testFromStatement() {
        // assertSelectQuery("SELECT id AS \"Идентификатор\", name \"Имя\" FROM local.users");
        // assertSelectQuery("SELECT id AS \"Идентификатор\", name \"Полное Имя\" FROM local.users");
        // assertWhereStatement("id = :id");
        // assertWhereStatement("id = ::id AND name = ::name");
        // assertWhereStatement("id = :id? AND name = :name?");
        // assertOrderByStatement("1 ASC, 2 DESC");
        //        assertSelectQuery("SELECT age AS bobj1.bobj2.bobj3");
        //assertSelectQuery("SELECT clientId::real");
        //assertSelectQuery("SELECT * FROM schemeName.clients cln CROSS JOIN schemeName.accounts acc WHERE cln.tcl_id = acc.id");
        //        assertSelectQueryWithAsClause("SELECT clientId::real");


        assertSelectQuery("SELECT cl.client_id, cl.last_name, cl.first_name, cl.middle_name "
                + "FROM test_scheme.pcl cl, test_scheme.o2cl o2c, test_scheme.cdcrc dr "
                + "WHERE dr.doc_id = ? AND dr.recipient_id = cl.client_id AND cl.status <> 0 AND o2c.client_id = cl.client_id AND o2c.operator_id = ? "
                + "AND cl.client_id IN (SELECT c2a.client_id FROM test_scheme.ccaa c2a, test_scheme.aaa a "
                + "WHERE c2a.account_id = a.id AND a.branch_id = ? AND a.account LIKE ?) "
                + "AND UPPER((CASE WHEN last_name IS NOT NULL THEN last_name ELSE '' END) || ' ' || (CASE WHEN first_name IS NOT NULL THEN first_name ELSE '' END) || ' ' || (CASE WHEN middle_name IS NOT NULL THEN middle_name ELSE '' END)"
                + ") LIKE ?");

    }

    private void assertSelectQueryWithAsClause(String query) { //AS client_name
        for (String asClause : new String[] {
                // TODO исправить парсер
                // "::date",
                // "::time",
                //" AT TIME ZONE 'UTC' AS datetime_alias,
                //" \"Первая\"\" колонка\"",
                " firstColumn",
                " AS firstColumn",
                " \"Первая колонка\"",
                " AS \"Первая колонка\"",
        }) {
            assertSelectQuery(query + asClause);
        }
    }

    private void assertWhereStatement(String whereStatement) {
        assertSelectQuery("SELECT 2 FROM tableName1 t1, tableName2 t2 WHERE " + whereStatement);
    }

    private void assertGroupByStatement(String groupByStatement) {
        assertSelectQuery("SELECT 2 FROM tableName1 t1, tableName2 t2 GROUP BY " + groupByStatement);
    }

    private void assertOrderByStatement(String orderByStatement) {
        assertSelectQuery("SELECT t1.c1, t1.c2, t2.c1, t2.c2 FROM tableName1 t1, tableName2 t2 ORDER BY " + orderByStatement);
    }

    private void assertSelectQuery(String query) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        assertQuery(query, selectQuery);
    }

    private void assertQuery(String query, SelectQuery selectQuery) {
        // assertEquals(query.replace(" ", ""), queryToString(selectQuery).replace(" ", ""));
        assertEquals(query, queryToString(selectQuery));
    }

    private String queryToString(SelectQuery selectQuery) {
        SqlBuilder builder = new SqlBuilder();
        selectQuery.view(builder);
        return builder.getQuery();
    }
}
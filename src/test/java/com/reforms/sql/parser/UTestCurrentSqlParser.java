package com.reforms.sql.parser;

import com.reforms.sql.expr.query.DeleteQuery;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
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


        assertDeleteQuery("DELETE \"deadline\", \"job\" FROM \"deadline\" LEFT JOIN \"job\" ON deadline.job_id = job.job_id");

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

    private void assertDeleteQuery(String query) {
        assertDeleteQuery(query, null);
    }

    private void assertDeleteQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        DeleteQuery deleteQuery = sqlParser.parseDeleteQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, deleteQuery);
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

    private void assertQuery(String query, Expression queryExpr) {
        // assertEquals(query.replace(" ", ""), queryToString(selectQuery).replace(" ", ""));
        assertEquals(query, queryToString(queryExpr));
    }

    private String queryToString(Expression queryExpr) {
        SqlBuilder builder = new SqlBuilder();
        queryExpr.view(builder);
        return builder.getQuery();
    }
}
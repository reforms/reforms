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
        //assertSelectQuery("SELECT age AS #t_bobj1.bobj2.bobj3");
        assertSelectQuery("SELECT * FROM ibank2.clients cln CROSS JOIN ibank2.accounts acc WHERE cln.client_id = acc.id");
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
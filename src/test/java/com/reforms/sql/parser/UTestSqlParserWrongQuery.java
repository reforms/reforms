package com.reforms.sql.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;
import com.reforms.sql.parser.SqlParser;

/**
 * SELECT client_id, name_cln, addr_cln
FROM ibank2.clients
WHERE (client_id, name_cln, addr_cln) = (60, 'архивный юрик', 'moskow');
 * @author evgenie
 */
public class UTestSqlParserWrongQuery {

    @Test
    public void testSingleArgSelectStatement() {
        assertWrongSelectQuery("SELECT ()");
    }

    private void assertWrongSelectQuery(String query) {
        SqlParser sqlParser = new SqlParser(query);
        try {
            SelectQuery selectQuery = sqlParser.parseSelectQuery();
            fail("Ожидается ошибка при разборе выражения: '" + query + "', а получено выражение: '" + queryToString(selectQuery));
        } catch (RuntimeException re) {
            assertEquals(IllegalStateException.class, re.getClass());
        }
    }

    private String queryToString(SelectQuery selectQuery) {
        SqlBuilder builder = new SqlBuilder();
        selectQuery.view(builder);
        return builder.getQuery();
    }
}
package com.reforms.sql.parser;

import com.reforms.sql.expr.query.CallQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Хранимки
 * @author evgenie
 */
public class UTestCallQueryParser {

    @Test
    public void testCallQueryParsing() {
        assertCallQuery("{call NOTH()}");
        assertCallQuery("{call NOTH(?)}");
        assertCallQuery("{call NOTH('a')}");
        assertCallQuery("{call NOTH('a', 'b')}");
        assertCallQuery("{call NOTH(?, ?)}");
        assertCallQuery("{call NOTH(:param1)}");
        assertCallQuery("{call NOTH(:param1, :param2)}");
        assertCallQuery("{? = CALL SEQ_ID(:param1, :param2)}");
        assertCallQuery("{? = CALL SEQ_ID()}");
        assertCallQuery("{(id, name) = CALL LOAD_CLIENT()}");
        assertCallQuery("{(:i#id, :s#name) = CALL LOAD_CLIENT()}");
        assertCallQuery("{(:i#bean.innerBean.id, :s#bean.innerBean.id) = CALL LOAD_CLIENT()}");
        assertCallQuery("{? = CALL shemaName.spaceName.SEQ_ID(:param1, :param2)}");
        assertCallQuery("{? = CALL shemaName.SEQ_ID(:param1, :param2)}");

    }

    private void assertCallQuery(String query) {
        assertCallQuery(query, null);
    }

    private void assertCallQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        CallQuery updateQuery = sqlParser.parseCallQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, updateQuery);
    }

    private void assertQuery(String query, CallQuery updateQuery) {
        assertEquals(query, queryToString(updateQuery));
    }

    private String queryToString(CallQuery updateQuery) {
        SqlBuilder builder = new SqlBuilder();
        updateQuery.view(builder);
        return builder.getQuery();
    }
}
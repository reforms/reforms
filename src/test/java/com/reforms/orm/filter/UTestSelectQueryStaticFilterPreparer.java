package com.reforms.orm.filter;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.reforms.orm.filter.FilterMap;
import com.reforms.orm.filter.SelectQueryPreparer;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

public class UTestSelectQueryStaticFilterPreparer {

    private static final String STATIC_FILTER_QUERY_1 =
            "SELECT 1 FROM goods WHERE id = :id";

    private static final String STATIC_FILTER_QUERY_ETALON_1 =
            "SELECT 1 FROM goods WHERE id = ?";

    @Test
    public void runTest_StaticFilter1() {
        assertStaticFilter(STATIC_FILTER_QUERY_1, STATIC_FILTER_QUERY_ETALON_1, new FilterMap("id", 1L));
    }

    private static final String STATIC_FILTER_QUERY_2 =
            "SELECT 1 FROM goods WHERE id = :id AND name = :name";

    private static final String STATIC_FILTER_QUERY_ETALON_2 =
            "SELECT 1 FROM goods WHERE id = ? AND name = ?";

    @Test
    public void runTest_StaticFilter2() {
        assertStaticFilter(STATIC_FILTER_QUERY_2, STATIC_FILTER_QUERY_ETALON_2, new FilterMap("id", 1L, "name", "Tost"));
    }

    private static final String STATIC_FILTER_QUERY_3 =
            "SELECT 1 FROM goods WHERE id = :id? AND name != :name? AND articul <> :articul? AND :price? = price";

    private static final String STATIC_FILTER_QUERY_ETALON_3 =
            "SELECT 1 FROM goods WHERE id IS NULL AND name IS NOT NULL AND articul IS NOT NULL AND price IS NULL";

    @Test
    public void runTest_StaticFilter3() {
        assertStaticFilter(STATIC_FILTER_QUERY_3, STATIC_FILTER_QUERY_ETALON_3, EMPTY_FILTER_MAP);
    }

    private static final String STATIC_FILTER_QUERY_4 =
            "SELECT 1 FROM goods WHERE id IN (:ids)";

    private static final String STATIC_FILTER_QUERY_ETALON_4 =
            "SELECT 1 FROM goods WHERE id IN (?, ?, ?)";

    @Test
    public void runTest_StaticFilter4() {
        assertStaticFilter(STATIC_FILTER_QUERY_4, STATIC_FILTER_QUERY_ETALON_4, new FilterMap("ids", Arrays.asList(1L, 2L, 3L)));
    }

    private static final String STATIC_FILTER_QUERY_5 =
            "SELECT 1 FROM goods WHERE id IN (:ids)";

    private static final String STATIC_FILTER_QUERY_ETALON_5 =
            "SELECT 1 FROM goods WHERE id IN (?, ?, ?, ?)";

    @Test
    public void runTest_StaticFilter5() {
        assertStaticFilter(STATIC_FILTER_QUERY_5, STATIC_FILTER_QUERY_ETALON_5, new FilterMap("ids", new long[] { 1, 2, 3, 4 }));
    }

    private static final String STATIC_FILTER_QUERY_6 =
            "SELECT 1 FROM goods WHERE id IN (:ids)";

    private static final String STATIC_FILTER_QUERY_ETALON_6 =
            "SELECT 1 FROM goods WHERE id IN (?, ?, ?, ?, ?)";

    @Test
    public void runTest_StaticFilter6() {
        assertStaticFilter(STATIC_FILTER_QUERY_6, STATIC_FILTER_QUERY_ETALON_6, new FilterMap("ids", new Iterable<Long>() {
            @Override
            public Iterator<Long> iterator() {
                return Arrays.asList(1L, 2L, 3L, 4L, 5L).iterator();
            }
        }));
    }

    private void assertStaticFilter(String query, String etalonQuery, FilterMap filters) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        SelectQueryPreparer queryPreaprer = new SelectQueryPreparer();
        queryPreaprer.prepare(selectQuery, filters);
        assertEquals(etalonQuery, selectQuery.toString());
    }

    @Test
    public void runTest_failStaticFilter2() {
        assertFailStaticFilter(STATIC_FILTER_QUERY_2, null);
        assertFailStaticFilter(STATIC_FILTER_QUERY_2, new FilterMap());
        assertFailStaticFilter(STATIC_FILTER_QUERY_2, new FilterMap("id", 1L));
        assertFailStaticFilter(STATIC_FILTER_QUERY_2, new FilterMap("name", "Tost"));
    }

    private void assertFailStaticFilter(String query, FilterMap filters) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        SelectQueryPreparer queryPreaprer = new SelectQueryPreparer();
        try {
            queryPreaprer.prepare(selectQuery, filters);
            fail("Ожидается, что выражение '" + query + "' имеет не валидный фильтр");
        } catch (IllegalStateException ise) {
            assertNotNull(ise.getMessage());
        }
    }

}

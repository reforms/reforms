package com.reforms.orm.filter;

import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.extractor.SelectQueryFilterPreparer;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

public class UTestSelectQueryDynamicFilterPreparer {

    private static final String DYNAMIC_FILTER_QUERY_1 =
            "SELECT 1 FROM goods WHERE id = ::id";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_1_V1 =
            "SELECT 1 FROM goods WHERE id = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_1_V2 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter1() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_1, DYNAMIC_FILTER_QUERY_ETALON_1_V1, new FilterMap("id", 1L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_1, DYNAMIC_FILTER_QUERY_ETALON_1_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_2 =
            "SELECT 1 FROM goods WHERE id = ::id AND name = ::name";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_2_V1 =
            "SELECT 1 FROM goods WHERE id = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_2_V2 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter2() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_2, DYNAMIC_FILTER_QUERY_ETALON_2_V1, new FilterMap("id", 1L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_2, DYNAMIC_FILTER_QUERY_ETALON_2_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_3 =
            "SELECT 1 FROM goods WHERE id = ::id AND name = ::name";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_3_V1 =
            "SELECT 1 FROM goods WHERE id = ? AND name = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_3_V2 =
            "SELECT 1 FROM goods WHERE id = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_3_V3 =
            "SELECT 1 FROM goods WHERE name = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_3_V4 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter3() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_3, DYNAMIC_FILTER_QUERY_ETALON_3_V1, new FilterMap("id", 1L, "name", "Тапочки"));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_3, DYNAMIC_FILTER_QUERY_ETALON_3_V2, new FilterMap("id", 1L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_3, DYNAMIC_FILTER_QUERY_ETALON_3_V3, new FilterMap("name", "Тапочки"));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_3, DYNAMIC_FILTER_QUERY_ETALON_3_V4, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_4 =
            "SELECT 1 FROM goods WHERE id IN (::ids)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_4_V1 =
            "SELECT 1 FROM goods WHERE id IN (?, ?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_4_V2 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter4() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_4, DYNAMIC_FILTER_QUERY_ETALON_4_V1, new FilterMap("ids", Arrays.asList(1L, 2L, 3L)));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_4, DYNAMIC_FILTER_QUERY_ETALON_4_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_5 =
            "SELECT 1 FROM goods WHERE id IN (::id1, ::id2)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_5_V1 =
            "SELECT 1 FROM goods WHERE id IN (?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_5_V2 =
            "SELECT 1 FROM goods WHERE id IN (?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_5_V3 =
            "SELECT 1 FROM goods WHERE id IN (?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_5_V4 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter5() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_5, DYNAMIC_FILTER_QUERY_ETALON_5_V1, new FilterMap("id1", 1L, "id2", 2L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_5, DYNAMIC_FILTER_QUERY_ETALON_5_V2, new FilterMap("id1", 1L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_5, DYNAMIC_FILTER_QUERY_ETALON_5_V3, new FilterMap("id2", 2L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_5, DYNAMIC_FILTER_QUERY_ETALON_5_V4, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_6 =
            "SELECT 1 FROM goods WHERE name LIKE ::name";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_6_V1 =
            "SELECT 1 FROM goods WHERE name LIKE ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_6_V2 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter6() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_6, DYNAMIC_FILTER_QUERY_ETALON_6_V1, new FilterMap("name", "Тапоч%"));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_6, DYNAMIC_FILTER_QUERY_ETALON_6_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_7 =
            "SELECT 1 FROM goods WHERE id BETWEEN ::id1 AND ::id2";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_7_V1 =
            "SELECT 1 FROM goods WHERE id BETWEEN ? AND ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_7_V2 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter7() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_7, DYNAMIC_FILTER_QUERY_ETALON_7_V1, new FilterMap("id1", 1L, "id2", 2L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_7, DYNAMIC_FILTER_QUERY_ETALON_7_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_8 =
            "SELECT 1 FROM goods WHERE (id, name, price) = (::id, ::name, ::price)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V1 =
            "SELECT 1 FROM goods WHERE (id, name, price) = (?, ?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V2 =
            "SELECT 1 FROM goods WHERE (id, name) = (?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V3 =
            "SELECT 1 FROM goods WHERE (id, price) = (?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V4 =
            "SELECT 1 FROM goods WHERE (name, price) = (?, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V5 =
            "SELECT 1 FROM goods WHERE (id) = (?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V6 =
            "SELECT 1 FROM goods WHERE (name) = (?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V7 =
            "SELECT 1 FROM goods WHERE (price) = (?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_8_V8 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter8() {
        long id = 1L;
        String name = "Тапочки";
        BigDecimal price = new BigDecimal("100.00");
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V1, new FilterMap("id", id, "name", name, "price", price));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V2, new FilterMap("id", id, "name", name));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V3, new FilterMap("id", id, "price", price));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V4, new FilterMap("name", name, "price", price));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V5, new FilterMap("id", id));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V6, new FilterMap("name", name));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V7, new FilterMap("price", price));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_8, DYNAMIC_FILTER_QUERY_ETALON_8_V8, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_9 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR id = BITAND(1, ::id)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_9_V1 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR id = BITAND(1, ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_9_V2 =
            "SELECT 1 FROM goods WHERE name LIKE '12'";

    @Test
    public void runTest_DynamicFilter9() {
        long id = 1L;
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_9, DYNAMIC_FILTER_QUERY_ETALON_9_V1, new FilterMap("id", id));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_9, DYNAMIC_FILTER_QUERY_ETALON_9_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_10 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR DECODE(BITAND(1, ::id), 1) IS NOT NULL";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_10_V1 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR DECODE(BITAND(1, ?), 1) IS NOT NULL";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_10_V2 =
            "SELECT 1 FROM goods WHERE name LIKE '12'";

    @Test
    public void runTest_DynamicFilter10() {
        long id = 1L;
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_10, DYNAMIC_FILTER_QUERY_ETALON_10_V1, new FilterMap("id", id));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_10, DYNAMIC_FILTER_QUERY_ETALON_10_V2, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_11 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR (CASE WHEN id = ::id1 OR id = ::id2 THEN 0 WHEN name = ::name THEN 1 ELSE id END) > 2";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_11_V1 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR (CASE WHEN id = ? OR id = ? THEN 0 ELSE id END) > 2";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_11_V2 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR (CASE WHEN id = ? THEN 0 WHEN name = ? THEN 1 ELSE id END) > 2";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_11_V3 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR (id) > 2";

    private static final String DYNAMIC_FILTER_QUERY_11_1 =
            "SELECT 1 FROM goods WHERE name LIKE '12' OR (CASE WHEN id = ::id1 OR id = ::id2 THEN 0 WHEN name = ::name THEN 1 END) > 2";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_11_V4 =
            "SELECT 1 FROM goods WHERE name LIKE '12'";

    @Test
    public void runTest_DynamicFilter11() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_11, DYNAMIC_FILTER_QUERY_ETALON_11_V1, new FilterMap("id1", -1L, "id2", -2L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_11, DYNAMIC_FILTER_QUERY_ETALON_11_V2, new FilterMap("id1", -1L, "name", "Тапочки"));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_11, DYNAMIC_FILTER_QUERY_ETALON_11_V3, EMPTY_FILTER_MAP);
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_11_1, DYNAMIC_FILTER_QUERY_ETALON_11_V4, EMPTY_FILTER_MAP);
    }

    private void assertDynamicFilter(String query, String etalonQuery, FilterMap filters) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        SelectQueryFilterPreparer queryPreaprer = OrmConfigurator.getInstance(SelectQueryFilterPreparer.class);
        queryPreaprer.prepare(selectQuery, filters);
        assertEquals(etalonQuery, selectQuery.toString());
    }

}

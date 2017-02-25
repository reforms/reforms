package com.reforms.orm.filter;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.filter.FilterMap;
import com.reforms.orm.filter.SelectQueryPreparer;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

public class UTestSearchGroupDynamicFilter {

    private static final String DYNAMIC_FILTER_QUERY_NOT_1 =
            "SELECT 1 FROM goods WHERE NOT (id = ::id)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_1 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter1() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_1, DYNAMIC_FILTER_QUERY_ETALON_NOT_1, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_NOT_2 =
            "SELECT 1 FROM goods WHERE NOT (id = ::id OR name = ::name)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V1 =
            "SELECT 1 FROM goods WHERE NOT (id = ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V2 =
            "SELECT 1 FROM goods WHERE NOT (name = ?)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V3 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter2() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_2, DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V1, new FilterMap("id", 1L));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_2, DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V2, new FilterMap("name", "Тапочки"));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_2, DYNAMIC_FILTER_QUERY_ETALON_NOT_2_V3, EMPTY_FILTER_MAP);
    }

    private static final String DYNAMIC_FILTER_QUERY_NOT_3 =
            "SELECT 1 FROM goods WHERE NOT (id = ::id OR name = ::name) AND price = ::price";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_3_V1 =
            "SELECT 1 FROM goods WHERE price = ?";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_3_V2 =
            "SELECT 1 FROM goods WHERE NOT (id = ?) AND price = ?";

    @Test
    public void runTest_DynamicFilter3() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_3, DYNAMIC_FILTER_QUERY_ETALON_NOT_3_V1, new FilterMap("price", BigDecimal.ZERO));
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_3, DYNAMIC_FILTER_QUERY_ETALON_NOT_3_V2, new FilterMap("id", 1L, "price",
                BigDecimal.ZERO));
    }

    private static final String DYNAMIC_FILTER_QUERY_NOT_4 =
            "SELECT 1 FROM goods WHERE NOT (id = ::id OR name = ::name) AND price = ::price OR (articul = ::articul OR NOT description = ::description)";

    private static final String DYNAMIC_FILTER_QUERY_ETALON_NOT_4_V1 =
            "SELECT 1 FROM goods";

    @Test
    public void runTest_DynamicFilter4() {
        assertDynamicFilter(DYNAMIC_FILTER_QUERY_NOT_4, DYNAMIC_FILTER_QUERY_ETALON_NOT_4_V1, EMPTY_FILTER_MAP);
    }

    private void assertDynamicFilter(String query, String etalonQuery, FilterMap filters) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        SelectQueryPreparer queryPreaprer = OrmConfigurator.get(SelectQueryPreparer.class);
        queryPreaprer.prepare(selectQuery, filters);
        assertEquals(etalonQuery, selectQuery.toString());
    }

}

package com.reforms.orm.filter;

import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.OrmContext;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

import org.junit.Test;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.assertEquals;

public class UTestSchemePreparer {

    private static final String QUERY_1 = "SELECT 1 FROM schemeName1.goods";

    private static final String QUERY_2 = "SELECT 1 FROM schemeName2.goods";

    private static final String QUERY_3 = "SELECT 1 FROM schemeName3.goods";

    private static final String QUERY_4 = "SELECT 1 FROM goods";

    private static final String QUERY_ETALON_1 = "SELECT 1 FROM local.goods";

    private static final String QUERY_ETALON_2 = "SELECT 1 FROM public.goods";

    private static final String QUERY_ETALON_3 = "SELECT 1 FROM shared.goods";

    private static final String QUERY_ETALON_4 = "SELECT 1 FROM free.goods";

    @Test
    public void runTest_CheckScheme() {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        ISchemeManager schemeManager = rCtx.getSchemeManager();
        SchemeManager sm = new SchemeManager();
        try {
            rCtx.setSchemeManager(sm);
            sm.putSchemeName("schemeName1", "local");
            sm.putSchemeName("schemeName2", "public");
            sm.putSchemeName("schemeName3", "shared");
            sm.setDefaultSchemeName("free");
            assertScheme(QUERY_1, QUERY_ETALON_1);
            assertScheme(QUERY_2, QUERY_ETALON_2);
            assertScheme(QUERY_3, QUERY_ETALON_3);
            assertScheme(QUERY_4, QUERY_ETALON_4);
        } finally {
            rCtx.setSchemeManager(schemeManager);
        }
    }

    private void assertScheme(String query, String expectedQuery) {
        SelectQueryPreparer queryPreaprer = new SelectQueryPreparer();
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        queryPreaprer.prepare(selectQuery, EMPTY_FILTER_MAP);
        assertEquals(expectedQuery, selectQuery.toString());
    }
}

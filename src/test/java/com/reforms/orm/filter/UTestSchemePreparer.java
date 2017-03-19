package com.reforms.orm.filter;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.filter.param.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.reforms.orm.CreateNewInstance;
import com.reforms.orm.IOrmContext;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.filter.SelectQueryPreparer;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

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
        SchemeManagerCreator schemeManagerCreator = new SchemeManagerCreator();
        IOrmContext context = getInstance(IOrmContext.class);
        try {
            context.changeSchemeManager(schemeManagerCreator);
            assertScheme(QUERY_1, QUERY_ETALON_1);
            assertScheme(QUERY_2, QUERY_ETALON_2);
            assertScheme(QUERY_3, QUERY_ETALON_3);
            assertScheme(QUERY_4, QUERY_ETALON_4);
        } finally {
            context.setSchemeManager(schemeManagerCreator.schemeManager);
        }
    }

    private void assertScheme(String query, String expectedQuery) {
        SelectQueryPreparer queryPreaprer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        queryPreaprer.prepare(selectQuery, EMPTY_FILTER_MAP);
        assertEquals(expectedQuery, selectQuery.toString());
    }

    private static class SchemeManagerCreator implements CreateNewInstance<ISchemeManager> {
        private ISchemeManager schemeManager;
        @Override
        public ISchemeManager createNew(ISchemeManager current) {
            this.schemeManager = current;
            SchemeManager sm = new SchemeManager();
            sm.putSchemeName("schemeName1", "local");
            sm.putSchemeName("schemeName2", "public");
            sm.putSchemeName("schemeName3", "shared");
            sm.setDefaultSchemeName("free");
            return sm;
        }
    }
}

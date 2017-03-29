package com.reforms.orm.extractor;

import com.reforms.orm.IOrmContext;
import com.reforms.orm.dao.filter.page.IPageFilter;
import com.reforms.orm.dao.filter.page.PageFilter;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

import org.junit.AfterClass;
import org.junit.Test;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.sql.db.DbType.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author evgenie
 */
public class UTestPageModifier {

    /***/
    private static String SIMPLE_SELECT_QUERY = "SELECT c1, c2 FROM t1 WHERE c3 > 4";

    @Test
    public void testModifyPostreSql() {
        String etalonQuery = SIMPLE_SELECT_QUERY + " LIMIT ? OFFSET ?";
        assertSelectQuery(DBT_POSTGRESQL, 10, 20, etalonQuery, SIMPLE_SELECT_QUERY, 10, 20);
    }

    @Test
    public void testModifyOracleSql() {
        String etalonQuery = "SELECT * FROM (SELECT c1, c2, ROWNUM __RN__ FROM (" + SIMPLE_SELECT_QUERY + ")) WHERE __RN__ > ? AND __RN__ <= ?";
        assertSelectQuery(DBT_ORACLE, 10, 20, etalonQuery, SIMPLE_SELECT_QUERY, 30, 20);
    }

    @AfterClass
    public static void restoreDbType() {
        setDefaultDbType(DBT_MIX);
    }

    private void assertSelectQuery(DbType dbType, Integer limit, Integer offset, String etalonQuery, String wordSqlQuery, Integer newLimit, Integer newOffset) {
        setDefaultDbType(dbType);
        PageModifier pageModifier = getInstance(PageModifier.class);
        SelectQuery selectQuery = parseSelectQuery(wordSqlQuery);
        PageFilter pageFilter = new PageFilter(limit, offset);
        IPageFilter newPageFilter = pageModifier.changeSelectQuery(selectQuery, pageFilter);
        assertEquals(newLimit, newPageFilter.getPageLimit());
        assertEquals(newOffset, newPageFilter.getPageOffset());
        String actualQuery = selectQuery.toString();
        assertEquals(etalonQuery, actualQuery);
    }

    private static void setDefaultDbType(DbType dbType) {
        IOrmContext context = getInstance(IOrmContext.class);
        context.setDefaultDbType(dbType);
    }

    private SelectQuery parseSelectQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }
}
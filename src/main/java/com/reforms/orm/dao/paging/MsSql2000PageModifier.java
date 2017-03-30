package com.reforms.orm.dao.paging;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.expr.query.SelectQuery;

/**
 * Prepapre sql-query to be ready for partition loading of data.
  // limit, offset and user ordering clause
  // SELECT TOP 10 cid,c1,c2,c3,c4,c5 FROM schemeKey.test_client WHERE cid NOT IN (SELECT TOP 100 cid FROM schemeKey.test_client ORDER BY c1,c2) ORDER BY c1,c2

  // limit, offset and no user ordering clause
  // SELECT TOP 10 cid,c1,c2,c3,c4,c5 FROM schemeKey.test_client WHERE cid NOT IN (SELECT TOP 100 cid FROM schemeKey.test_client ORDER BY cid) ORDER BY cid

  // limit 20 only
  // SELECT TOP 20 [cid],c1,c2,c3,c4,c5 FROM schemeKey.test_client ORDER BY cid
 * @author evgenie
 */
@ThreadSafe
public class MsSql2000PageModifier implements IPageModifier {

    private static final String MSSQL_2000_SQL_PAGE_TEMPLATE_LIMIT_ONLY = "SELECT TOP {0} {1}, {2}";

    @Override
    public IPageFilter addPagingQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        // CASE1: SELECT id, c1, c2, c3, c4, c5 FROM schemeKey.test_client;
        throw new IllegalStateException("Not implemented yet: MSSQL2000");
    }
}
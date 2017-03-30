package com.reforms.orm.dao.paging;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.expr.query.SelectQuery;

/**
 * Prepapre sql-query to be ready for partition loading of data.
 * @author evgenie
 */
@ThreadSafe
public class Db2PageModifier implements IPageModifier {

    @Override
    public IPageFilter addPagingQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        throw new IllegalStateException("Not implemented yet: DB2");
    }
}
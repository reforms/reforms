package com.reforms.orm.dao.paging;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.expr.query.SelectQuery;

/**
 * Prepapre sql-query to be ready for partition loading of data.
    SELECT  *
    FROM    ( SELECT    ROW_NUMBER() OVER ( ORDER BY OrderDate ) AS RowNum, *
              FROM      Orders
              WHERE     OrderDate >= '1980-01-01'
            ) AS RowConstrainedResult
    WHERE   RowNum >= 1
        AND RowNum < 20
    ORDER BY RowNum
 * @author evgenie
 */
@ThreadSafe
public class MsSql2012PageModifier implements IPageModifier {

    @Override
    public IPageFilter addPagingQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        throw new IllegalStateException("Not implemented yet: MSSQL2012");
    }
}
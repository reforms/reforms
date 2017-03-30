package com.reforms.orm.dao.paging;

import com.reforms.sql.expr.query.SelectQuery;

/**
 * Контракт на постраничную разбивку.
 * @author evgenie
 */
public interface IPageModifier {

    public IPageFilter addPagingQuery(SelectQuery selectQuery, IPageFilter pageFilter);
}

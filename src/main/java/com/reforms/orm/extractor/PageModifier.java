package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.paging.IPageFilter;
import com.reforms.orm.dao.paging.IPageModifier;
import com.reforms.orm.dao.paging.PageModifierFactory;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;

import static com.reforms.orm.OrmConfigurator.getInstance;

/**
 * Prepapre sql-query to be ready for partition loading of data.
 *
 * @author evgenie
 */
@ThreadSafe
public class PageModifier {

    public PageModifier() {
    }

    public IPageFilter changeSelectQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        if (!pageFilter.hasPageFilter()) {
            return pageFilter;
        }
        DbTypeExtractor dbTypeExtractor = OrmConfigurator.getInstance(DbTypeExtractor.class);
        DbType dbType = dbTypeExtractor.extractDbType(selectQuery);
        PageModifierFactory pageModifierFactory = getInstance(PageModifierFactory.class);
        IPageModifier pageModifier = pageModifierFactory.getPageModifier(dbType);
        if (pageModifier == null) {
            throw new IllegalStateException("Постраничная разбивка временно не поддерживается для СУБД с типом '" + dbType + "'");
        }
        IPageFilter newPageFilter = pageModifier.addPagingQuery(selectQuery, pageFilter);
        return newPageFilter;
    }
}
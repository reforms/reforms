package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.filter.page.IPageFilter;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.PageStatement;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;

import static com.reforms.sql.db.DbType.MIX;
import static com.reforms.sql.db.DbType.POSTGRESQL;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_LIMIT;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_OFFSET;

/**
 * Prepapre sql-query to be ready for partition loading of data.
 * @author evgenie
 */
@ThreadSafe
public class PageModifier {

    public PageModifier() {
    }

    public void changeSelectQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        DbTypeExtractor dbTypeExtractor = OrmConfigurator.getInstance(DbTypeExtractor.class);
        DbType dbType = dbTypeExtractor.extractDbType(selectQuery);
        if (POSTGRESQL == dbType || MIX == dbType) {
            changeSelectQueryWhenPostgreSql(selectQuery, pageFilter);
            return;
        }
        throw new IllegalStateException("Постраничная разбивка временно не поддерживается для СУБД с типом '" + dbType + "'");
    }

    private void changeSelectQueryWhenPostgreSql(SelectQuery selectQuery, IPageFilter pageFilter) {
        PageStatement pageStatement = new PageStatement();
        Object pageLimit = pageFilter.getPageLimit();
        if (pageLimit != null) {
            LimitExpression limitExpr = new LimitExpression();
            limitExpr.setLimitExpr(new PageQuestionExpression(PQE_LIMIT));
            pageStatement.setLimitExpr(limitExpr);
            selectQuery.setPageStatement(pageStatement);
        }
        Object pageOffset = pageFilter.getPageOffset();
        if (pageOffset != null) {
            OffsetExpression offsetExpr = new OffsetExpression();
            offsetExpr.setOffsetExpr(new PageQuestionExpression(PQE_OFFSET));
            pageStatement.setOffsetExpr(offsetExpr);
            selectQuery.setPageStatement(pageStatement);
        }
    }

}

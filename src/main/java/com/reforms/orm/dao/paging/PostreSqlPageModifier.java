package com.reforms.orm.dao.paging;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.PageStatement;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;

import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_LIMIT;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_OFFSET;

/**
 * Prepapre sql-query to be ready for partition loading of data.
 * @author evgenie
 */
@ThreadSafe
public class PostreSqlPageModifier implements IPageModifier {

    @Override
    public IPageFilter addPagingQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
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
        return pageFilter;
    }
}

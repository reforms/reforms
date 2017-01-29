package com.reforms.orm.extractor;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_EXPRESSION;
import static com.reforms.sql.expr.term.value.ValueExpressionType.*;

import java.util.ArrayList;
import java.util.List;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.value.ValueExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

public class FilterExpressionExtractor extends SqlBuilder {

    private List<ValueExpression> filterExprs;

    public List<ValueExpression> extractFilterExpressions(SelectQuery selectQuery) {
        filterExprs = new ArrayList<>();
        selectQuery.view(this);
        return filterExprs;
    }

    @Override
    public SqlBuilder appendExpression(Expression expr) {
        if (expr != null) {
            if (ET_VALUE_EXPRESSION == expr.getType()) {
                ValueExpression valueExpr = (ValueExpression) expr;
                if (VET_FILTER == valueExpr.getValueExprType() || VET_QUESTION == valueExpr.getValueExprType()
                        || VET_PAGE_QUESTION == valueExpr.getValueExprType()) {
                    filterExprs.add(valueExpr);
                }
            }
        }
        super.appendExpression(expr);
        return this;
    }

}

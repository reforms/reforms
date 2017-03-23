package com.reforms.orm.extractor;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.value.ValueExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_EXPRESSION;
import static com.reforms.sql.expr.term.value.ValueExpressionType.*;

public class ValueExpressionExtractor extends SqlBuilder {

    private List<ValueExpression> valueExprs;

    public List<ValueExpression> extractFilterExpressions(Expression query) {
        valueExprs = new ArrayList<>();
        query.view(this);
        return valueExprs;
    }

    @Override
    public SqlBuilder appendExpression(Expression expr) {
        if (expr != null) {
            if (ET_VALUE_EXPRESSION == expr.getType()) {
                ValueExpression valueExpr = (ValueExpression) expr;
                if (VET_FILTER == valueExpr.getValueExprType() || VET_QUESTION == valueExpr.getValueExprType()
                        || VET_PAGE_QUESTION == valueExpr.getValueExprType()) {
                    valueExprs.add(valueExpr);
                }
            }
        }
        super.appendExpression(expr);
        return this;
    }

}

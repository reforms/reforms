package com.reforms.orm.extractor;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_EXPRESSION;

import java.util.ArrayList;
import java.util.List;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.value.FilterExpression;
import com.reforms.sql.expr.term.value.QuestionExpression;
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
                if (expr instanceof FilterExpression || expr instanceof QuestionExpression) {
                    ValueExpression filterExpr = (ValueExpression) expr;
                    filterExprs.add(filterExpr);
                }
            }
        }
        super.appendExpression(expr);
        return this;
    }

}

package com.reforms.sql.expr.term.value;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_EXPRESSION;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

public class ValueExpression extends SelectableExpression {

    private String value;

    private ValueExpressionType valueExprType;

    public ValueExpression(String value, ValueExpressionType valueExprType) {
        this.value = value;
        this.valueExprType = valueExprType;
    }

    public String getValue() {
        return value;
    }

    public ValueExpressionType getValueExprType() {
        return valueExprType;
    }

    @Override
    public ExpressionType getType() {
        return ET_VALUE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(value);
    }
}
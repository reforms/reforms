package com.reforms.sql.expr.term.value;

public class NumericExpression extends ValueExpression {

    public NumericExpression(String numberValue) {
        super(numberValue, ValueExpressionType.VET_NUMERIC);
    }
}
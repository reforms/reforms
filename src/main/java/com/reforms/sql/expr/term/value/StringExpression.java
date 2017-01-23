package com.reforms.sql.expr.term.value;

public class StringExpression extends ValueExpression {

    public StringExpression(String stringValue) {
        super(stringValue, ValueExpressionType.VET_STRING);
    }
}
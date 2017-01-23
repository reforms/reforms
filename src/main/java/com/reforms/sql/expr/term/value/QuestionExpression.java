package com.reforms.sql.expr.term.value;

public class QuestionExpression extends ValueExpression {

    public QuestionExpression() {
        super("?", ValueExpressionType.VET_QUESTION);
    }
}
package com.reforms.sql.expr.term.value;

import static com.reforms.sql.expr.term.SqlWords.SW_FALSE;

/**
 * FALSE
 * @author evgenie
 */
public class FalseExpression extends ValueExpression {

    public FalseExpression() {
        this(SW_FALSE);
    }

    public FalseExpression(String falseWord) {
        super(falseWord, ValueExpressionType.VET_FALSE);
    }

}
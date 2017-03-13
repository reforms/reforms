package com.reforms.sql.expr.term.value;

import static com.reforms.sql.parser.SqlWords.SW_TRUE;

/**
 * TRUE
 * @author evgenie
 */
public class TrueExpression extends ValueExpression {

    public TrueExpression() {
        this(SW_TRUE);
    }

    public TrueExpression(String trueWord) {
        super(trueWord, ValueExpressionType.VET_TRUE);
    }

}
package com.reforms.sql.expr.term.value;

import static com.reforms.sql.parser.SqlWords.SW_NULL;

/**
 * NULL
 * @author evgenie
 */
public class NullExpression extends ValueExpression {

    public NullExpression() {
        this(SW_NULL);
    }

    public NullExpression(String nullWord) {
        super(nullWord, ValueExpressionType.VET_NULL);
    }

}
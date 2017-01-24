package com.reforms.sql.expr.term.from;

import com.reforms.sql.expr.term.Expression;

/**
 *
 * @author evgenie
 */
public abstract class TableReferenceExpression extends Expression {

    private String separator;

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
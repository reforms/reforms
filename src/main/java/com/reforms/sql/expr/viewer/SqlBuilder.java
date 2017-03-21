package com.reforms.sql.expr.viewer;

import com.reforms.sql.expr.term.Expression;

import java.util.List;

/**
 *
 * @author evgenie
 */
public class SqlBuilder {

    private StringBuilder query = new StringBuilder();

    public SqlBuilder appendSpace() {
        if (query.length() != 0 && ' ' != query.charAt(query.length() - 1) && '(' != query.charAt(query.length() - 1)) {
            query.append(" ");
        }
        return this;
    }

    public SqlBuilder append(Object value) {
        if (value != null) {
            query.append(value);
        }
        return this;
    }

    public SqlBuilder appendWord(String word) {
        if (word != null) {
            append(word);
        }
        return this;
    }

    public SqlBuilder appendExpression(Expression expr) {
        if (expr != null) {
            if (expr.isChangedExpression()) {
                appendExpression(expr.getChangedExpr());
            } else {
                if (expr.isSpacable()) {
                    appendSpace();
                }
                if (expr.isWrapped()) {
                    append("(");
                }
                expr.view(this);
                if (expr.isWrapped()) {
                    append(")");
                }
            }
        }
        return this;
    }

    public SqlBuilder appendExpressions(List<? extends Expression> exprs, String separator) {
        if (!exprs.isEmpty()) {
            appendExpression(exprs.get(0));
            for (int index = 1; index < exprs.size(); index++) {
                append(separator);
                appendExpression(exprs.get(index));
            }
        }
        return this;
    }

    public String getQuery() {
        return query.toString();
    }
}

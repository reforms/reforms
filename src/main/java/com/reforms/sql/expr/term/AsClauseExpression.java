package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_AS_CLAUSE_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

public class AsClauseExpression extends Expression {

    private String asWord;

    private String alias;

    public String getAsWord() {
        return asWord;
    }

    public void setAsWord(String asWord) {
        this.asWord = asWord;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public ExpressionType getType() {
        return ET_AS_CLAUSE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (alias != null) {
            if (asWord != null) {
                sqlBuilder.appendSpace();
                sqlBuilder.appendWord(asWord);
            }
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(alias);
        }
    }
}
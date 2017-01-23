package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_LIKE_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.*;

public class LikePredicateExpression extends PredicateExpression {

    private Expression matchValueExpr;

    private boolean useNotWord;

    private Expression patternExpr;

    private Expression escapeExpr;

    public Expression getMatchValueExpr() {
        return matchValueExpr;
    }

    public void setMatchValueExpr(Expression matchValueExpr) {
        this.matchValueExpr = matchValueExpr;
    }

    public boolean isUseNotWord() {
        return useNotWord;
    }

    public void setUseNotWord(boolean useNotWord) {
        this.useNotWord = useNotWord;
    }

    public Expression getPatternExpr() {
        return patternExpr;
    }

    public void setPatternExpr(Expression patternExpr) {
        this.patternExpr = patternExpr;
    }

    public Expression getEscapeExpr() {
        return escapeExpr;
    }

    public void setEscapeExpr(Expression escapeExpr) {
        this.escapeExpr = escapeExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_LIKE_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(matchValueExpr);
        if (isUseNotWord()) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_NOT);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_LIKE);
        sqlBuilder.appendExpression(patternExpr);
        if (escapeExpr != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_ESCAPE);
            sqlBuilder.appendExpression(escapeExpr);
        }
    }
}

package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_LIKE_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_LIKE;

public class LikePredicateExpression extends PredicateExpression {

    private Expression matchValueExpr;

    private String notWord;

    private String likeWord = SW_LIKE;

    private Expression patternExpr;

    private EscapeExpression escapeExpr;

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public String getLikeWord() {
        return likeWord;
    }

    public void setLikeWord(String likeWord) {
        this.likeWord = likeWord;
    }

    public Expression getMatchValueExpr() {
        return matchValueExpr;
    }

    public void setMatchValueExpr(Expression matchValueExpr) {
        this.matchValueExpr = matchValueExpr;
    }

    public Expression getPatternExpr() {
        return patternExpr;
    }

    public void setPatternExpr(Expression patternExpr) {
        this.patternExpr = patternExpr;
    }

    public EscapeExpression getEscapeExpr() {
        return escapeExpr;
    }

    public void setEscapeExpr(EscapeExpression escapeExpr) {
        this.escapeExpr = escapeExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_LIKE_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(matchValueExpr);
        if (notWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(notWord);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(likeWord);
        sqlBuilder.appendExpression(patternExpr);
        sqlBuilder.appendExpression(escapeExpr);
    }
}
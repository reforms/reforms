package com.reforms.sql.expr.term.page;

import static com.reforms.sql.expr.term.ExpressionType.ET_LIMIT_EXPRESSION;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Example:
 *  - 'LIMIT 10'
 *  - 'LIMIT ALL'
 * @author evgenie
 */
public class LimitExpression extends Expression {

    private String limitWord;

    private Expression limitExpr;

    public String getLimitWord() {
        return limitWord;
    }

    public void setLimitWord(String limitWord) {
        this.limitWord = limitWord;
    }

    public Expression getLimitExpr() {
        return limitExpr;
    }

    public void setLimitExpr(Expression limitExpr) {
        this.limitExpr = limitExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_LIMIT_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(limitWord);
        sqlBuilder.appendExpression(limitExpr);
    }
}

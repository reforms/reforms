package com.reforms.sql.expr.term.predicate;

import static com.reforms.sql.expr.term.ExpressionType.ET_QUANTIFIED_COMPARISON_PREDICATE_EXPRESSION;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

public class QuantifiedComparisonPredicateExpression extends PredicateExpression {

    private Expression baseExpr;

    private ComparisonOperator compOperatorType;

    private String quantifierWord;

    private SelectQuery subSelectQuery;

    public Expression getBaseExpr() {
        return baseExpr;
    }

    public void setBaseExpr(Expression baseExpr) {
        this.baseExpr = baseExpr;
    }

    public ComparisonOperator getCompOperatorType() {
        return compOperatorType;
    }

    public void setCompOperatorType(ComparisonOperator compOperatorType) {
        this.compOperatorType = compOperatorType;
    }

    public String getQuantifierWord() {
        return quantifierWord;
    }

    public void setQuantifierWord(String quantifierWord) {
        this.quantifierWord = quantifierWord;
    }

    public SelectQuery getSubSelectQuery() {
        return subSelectQuery;
    }

    public void setSubSelectQuery(SelectQuery subSelectQuery) {
        this.subSelectQuery = subSelectQuery;
    }

    @Override
    public ExpressionType getType() {
        return ET_QUANTIFIED_COMPARISON_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(baseExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(compOperatorType.getOperator());
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(quantifierWord);
        sqlBuilder.appendExpression(subSelectQuery);
    }
}
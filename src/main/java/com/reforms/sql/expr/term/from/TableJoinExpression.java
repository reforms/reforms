package com.reforms.sql.expr.term.from;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_JOIN_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_ON;

/**
 *
 * @author palihov
 */
public class TableJoinExpression extends TableReferenceExpression {

    private String joinWords;

    private TableJoinTypes joinType;

    private TableReferenceExpression tableRefExpr;

    private Expression onConditionExpr;

    public String getJoinWords() {
        return joinWords;
    }

    public void setJoinWords(String joinWords) {
        this.joinWords = joinWords;
    }

    public TableJoinTypes getJoinType() {
        return joinType;
    }

    public void setJoinType(TableJoinTypes joinType) {
        this.joinType = joinType;
    }

    public TableReferenceExpression getTableRefExpr() {
        return tableRefExpr;
    }

    public void setTableRefExpr(TableReferenceExpression tableRefExpr) {
        this.tableRefExpr = tableRefExpr;
    }

    public Expression getOnConditionExpr() {
        return onConditionExpr;
    }

    public void setOnConditionExpr(Expression onConditionExpr) {
        this.onConditionExpr = onConditionExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_TABLE_JOIN_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(joinWords);
        sqlBuilder.appendExpression(tableRefExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_ON);
        sqlBuilder.appendExpression(onConditionExpr);
    }
}
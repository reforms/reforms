package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_OVER_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_OVER;

/**
 * GRAMMAR:
 * OVER ( [ PARTITION BY value_expression , ... [ n ] ] order_by_clause )
 *
 * MSSQL
 *
 * @author evgenie
 */
public class OverStatement extends Expression {

    private String overWord = SW_OVER;

    private PartitionByStatement partitionByStatement;

    private OrderByStatement orderByStatement;

    public String getOverWord() {
        return overWord;
    }

    public void setOverWord(String overWord) {
        this.overWord = overWord;
    }

    public PartitionByStatement getPartitionByStatement() {
        return partitionByStatement;
    }

    public void setPartitionByStatement(PartitionByStatement partitionByStatement) {
        this.partitionByStatement = partitionByStatement;
    }

    public OrderByStatement getOrderByStatement() {
        return orderByStatement;
    }

    public void setOrderByStatement(OrderByStatement orderByStatement) {
        this.orderByStatement = orderByStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_OVER_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(overWord);
        sqlBuilder.append("(");
        sqlBuilder.appendExpression(partitionByStatement);
        sqlBuilder.appendExpression(orderByStatement);
        sqlBuilder.append(")");
    }
}
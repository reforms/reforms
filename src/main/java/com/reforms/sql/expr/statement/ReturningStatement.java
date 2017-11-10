package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_RETURNING_STATEMENT;

/**
 * [ RETURNING * | output_expression [ [ AS ] output_name ] [, ...] ]
 * @author evgenie
 */
public class ReturningStatement extends Expression {

    private final String returningKeyword;

    private final List<SelectableExpression> returningExprs;

    public ReturningStatement(String returningKeyword, List<SelectableExpression> returningExprs) {
        this.returningKeyword = returningKeyword;
        this.returningExprs = returningExprs;
    }

    public String getReturningKeyword() {
        return returningKeyword;
    }

    public List<SelectableExpression> getReturningExprs() {
        return returningExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_RETURNING_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(returningKeyword);
        sqlBuilder.appendExpressions(returningExprs, ",");
    }
}
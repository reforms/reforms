package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableReferenceExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_FROM_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_FROM;

public class FromStatement extends Expression {

    private List<TableReferenceExpression> tableRefExprs = new ArrayList<>();

    public List<TableReferenceExpression> getTableRefExprs() {
        return tableRefExprs;
    }

    public boolean addTableRefExpr(TableReferenceExpression tableRefExpr) {
        return tableRefExprs.add(tableRefExpr);
    }

    public void setTableRefExprs(List<TableReferenceExpression> tableRefExprs) {
        this.tableRefExprs = tableRefExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_FROM_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_FROM);
        for (TableReferenceExpression tableRefExpr : tableRefExprs) {
            sqlBuilder.appendExpression(tableRefExpr);
            sqlBuilder.appendWord(tableRefExpr.getSeparator());
        }
    }
}

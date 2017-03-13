package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableReferenceExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.parser.SqlWords.SW_FROM;

import static com.reforms.sql.expr.term.ExpressionType.ET_FROM_STATEMENT;

public class FromStatement extends Expression {

    /** FROM */
    private String fromWord = SW_FROM;

    private List<TableReferenceExpression> tableRefExprs = new ArrayList<>();

    public String getFromWord() {
        return fromWord;
    }

    public void setFromWord(String fromWord) {
        this.fromWord = fromWord;
    }

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
        sqlBuilder.appendWord(fromWord);
        for (TableReferenceExpression tableRefExpr : tableRefExprs) {
            sqlBuilder.appendExpression(tableRefExpr);
            sqlBuilder.appendWord(tableRefExpr.getSeparator());
        }
    }
}

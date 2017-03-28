package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableReferenceExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_USING_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_USING;

/**
 * MySql GRAMMAR:
 * USING table_references
 * @author evgenie
 */
public class UsingStatement extends Expression {

    private String usingWord = SW_USING;

    private List<TableReferenceExpression> tableRefExprs = new ArrayList<>();

    public String getUsingWord() {
        return usingWord;
    }

    public void setUsingWord(String usingWord) {
        this.usingWord = usingWord;
    }

    public List<TableReferenceExpression> getTableRefExprs() {
        return tableRefExprs;
    }

    public void setTableRefExprs(List<TableReferenceExpression> tableRefExprs) {
        this.tableRefExprs = tableRefExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_USING_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(usingWord);
        for (TableReferenceExpression tableRefExpr : tableRefExprs) {
            sqlBuilder.appendExpression(tableRefExpr);
            sqlBuilder.appendWord(tableRefExpr.getSeparator());
        }
    }
}
package com.reforms.sql.expr.statement;

import static com.reforms.sql.expr.term.ExpressionType.ET_DELETE_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_DELETE_FROM;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * DELETE FROM tableName
 * GRAMMAR:
 * DELETE FROM <table name>
 * @author evgenie
 */
public class DeleteStatement extends Expression {

    /** DELETE FROM */
    private String deleteFromWords = SW_DELETE_FROM;

    /** TABLE INFO */
    private TableExpression tableExpr;

    public String getDeleteFromWords() {
        return deleteFromWords;
    }

    public void setDeleteFromWords(String deleteFromWords) {
        this.deleteFromWords = deleteFromWords;
    }

    public TableExpression getTableExpr() {
        return tableExpr;
    }

    public void setTableExpr(TableExpression tableExpr) {
        this.tableExpr = tableExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_DELETE_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(deleteFromWords);
        sqlBuilder.appendExpression(tableExpr);
    }
}
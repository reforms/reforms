package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_SET_CLAUSE_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_UPDATE;

/**
 * UPDATE tableName
 * GRAMMAR:
 * UPDATE <table name>
 * @author evgenie
 */
public class UpdateStatement extends Expression {

    /** UPDATE */
    private String updateWord = SW_UPDATE;

    /** TABLE INFO */
    private TableExpression tableExpr;

    public String getUpdateWord() {
        return updateWord;
    }

    public void setUpdateWord(String updateWord) {
        this.updateWord = updateWord;
    }

    public TableExpression getTableExpr() {
        return tableExpr;
    }

    public void setTableExpr(TableExpression tableExpr) {
        this.tableExpr = tableExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_SET_CLAUSE_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(updateWord);
        sqlBuilder.appendExpression(tableExpr);
    }
}
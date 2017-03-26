package com.reforms.sql.expr.statement;

import static com.reforms.sql.expr.term.ExpressionType.ET_INSERT_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_INSERT_INTO;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.ValueListExpression;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * INSERT INTO tableName
 * GRAMMAR: <insert statement>    ::=   INSERT INTO <table name>  <insert columns and source>
 * @author evgenie
 */
public class InsertStatement extends Expression {

    /** INSERT INTO */
    private String insertIntoWords = SW_INSERT_INTO;

    /** TABLE INFO */
    private TableExpression tableExpr;

    /** список колонок для вставки */
    private ValueListExpression insertColumnNamesExpr;

    /** Вставляемые колонки */
    private Expression insertValuesExpr;

    public String getInsertIntoWords() {
        return insertIntoWords;
    }

    public void setInsertIntoWords(String insertIntoWords) {
        this.insertIntoWords = insertIntoWords;
    }

    public TableExpression getTableExpr() {
        return tableExpr;
    }

    public void setTableExpr(TableExpression tableExpr) {
        this.tableExpr = tableExpr;
    }

    public ValueListExpression getInsertColumnNamesExpr() {
        return insertColumnNamesExpr;
    }

    public void setInsertColumnNamesExpr(ValueListExpression insertColumnNamesExpr) {
        this.insertColumnNamesExpr = insertColumnNamesExpr;
    }

    public Expression getInsertValuesExpr() {
        return insertValuesExpr;
    }

    public void setInsertValuesExpr(Expression insertValuesExpr) {
        this.insertValuesExpr = insertValuesExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_INSERT_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(insertIntoWords);
        sqlBuilder.appendExpression(tableExpr);
        sqlBuilder.appendExpression(insertColumnNamesExpr);
        sqlBuilder.appendExpression(insertValuesExpr);
    }
}
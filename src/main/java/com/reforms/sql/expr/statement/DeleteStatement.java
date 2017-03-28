package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_DELETE_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_DELETE;

/**
 * DELETE [tableAlias1, tableAlias2]
 * GRAMMAR:
 * DELETE [tableAlias1, tableAlias2]
 * @author evgenie
 */
public class DeleteStatement extends Expression {

    /** DELETE */
    private String deleteWord = SW_DELETE;

    private List<TableExpression> tableExprs;

    public String getDeleteWord() {
        return deleteWord;
    }

    public void setDeleteWord(String deleteWord) {
        this.deleteWord = deleteWord;
    }

    public List<TableExpression> getTableExprs() {
        return tableExprs;
    }

    public void setTableExprs(List<TableExpression> tableExprs) {
        this.tableExprs = tableExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_DELETE_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(deleteWord);
        sqlBuilder.appendExpressions(tableExprs, ",");
    }
}
package com.reforms.orm.extractor;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_EXPRESSION;

/**
 * Возвращает список TableExpression
 * @author evgenie
 */
public class TableExpressionExtractor extends SqlBuilder {

    private List<TableExpression> tableExprs;

    public List<TableExpression> extractFilterExpressions(SelectQuery selectQuery) {
        tableExprs = new ArrayList<>();
        selectQuery.view(this);
        return tableExprs;
    }

    @Override
    public SqlBuilder appendExpression(Expression expr) {
        if (expr != null) {
            if (ET_TABLE_EXPRESSION == expr.getType() && expr instanceof TableExpression) {
                tableExprs.add((TableExpression) expr);
            }
        }
        super.appendExpression(expr);
        return this;
    }
}
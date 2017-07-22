package com.reforms.orm.extractor;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Обеспечивает апи по обработки указанных выражений
 * @author evgenie
 */
class ExpressionScanner extends SqlBuilder {

    private IExpressionProcessor procesor;
    private boolean scanNext = true;

    void scan(Expression selectQuery, IExpressionProcessor procesor) {
        this.procesor = procesor;
        selectQuery.view(this);
    }

    @Override
    public SqlBuilder appendExpression(Expression expr) {
        if (expr != null && scanNext) {
            ExpressionType origExprType = expr.getType();
            if (procesor.accept(origExprType)) {
                scanNext = procesor.process(origExprType, expr);
            }
        }
        if (scanNext) {
            super.appendExpression(expr);
        }
        return this;
    }
}
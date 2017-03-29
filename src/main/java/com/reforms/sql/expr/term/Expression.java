package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.IExpressionChanger;
import com.reforms.sql.expr.viewer.ISqlViewer;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * @author evgenie
 */
public abstract class Expression implements ISqlViewer, IExpressionChanger {

    private boolean wrapped;

    private boolean spacable = true;

    private boolean changedFlag;

    /** TODO: Архитектурный костыль.
     *  На самом деле нужно убрать это поле и в каждом наследнике реализовать changeExpression
     *  заменяя oldChildExpr на newChildExpr.
     *  Задача не простая, возможны проблемы с типами, или необходимо предложить иное решение,
     *  например, с трансформерами - кода будет в 2 раза больше.
     */
    private Expression changedExpr;

    public boolean isWrapped() {
        return wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    public abstract ExpressionType getType();

    public boolean isChangedExpression() {
        return changedFlag;
    }

    public Expression getChangedExpr() {
        return changedExpr;
    }

    public void setSpacable(boolean spacable) {
        this.spacable = spacable;
    }

    public boolean isSpacable() {
        return spacable;
    }

    @Override
    public boolean changeExpression(Expression oldChildExpr, Expression newChildExpr) {
        oldChildExpr.changedExpr = newChildExpr;
        oldChildExpr.changedFlag = true;
        return true;
    }

    @Override
    public String toString() {
        SqlBuilder builder = new SqlBuilder();
        builder.appendExpression(this);
        return builder.getQuery();
    }
}

package com.reforms.sql.expr.viewer;

import com.reforms.sql.expr.term.Expression;

/**
 * Контракт на изменения выражения внутри самого выражения
 * @author evgenie
 */
public interface IExpressionChanger {

    public boolean changeExpression(Expression oldChildExpr, Expression newChildExpr);
}

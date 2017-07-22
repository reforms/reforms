package com.reforms.orm.extractor;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;

/**
 * Обработчик схем
 * @author evgenie
 */
public interface IExpressionProcessor {

    boolean accept(ExpressionType exprType);

    boolean process(ExpressionType exprType, Expression expr);
}

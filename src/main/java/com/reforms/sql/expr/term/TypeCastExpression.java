package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_TYPE_CAST_EXPRESSION;

/**
 * ::TYPE_CAST
 * PostreSql
 * @author evgenie
 */
public class TypeCastExpression extends Expression {

    private FuncExpression typeInfoExpr;

    public TypeCastExpression() {
        setSpacable(false);
    }

    public FuncExpression getTypeInfoExpr() {
        return typeInfoExpr;
    }

    public void setTypeInfoExpr(FuncExpression typeInfoExpr) {
        this.typeInfoExpr = typeInfoExpr;
        typeInfoExpr.setSpacable(false);
    }

    @Override
    public ExpressionType getType() {
        return ET_TYPE_CAST_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.append("::");
        sqlBuilder.appendExpression(typeInfoExpr);
    }
}
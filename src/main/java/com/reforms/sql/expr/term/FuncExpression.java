package com.reforms.sql.expr.term;

import com.reforms.sql.expr.statement.OverStatement;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_FUNC_EXPRESSION;

/**
 *
 * @author evgenie
 */
public class FuncExpression extends SelectableExpression {

    private boolean shortStyle;

    private String name;

    private ValueListExpression args;

    private OverStatement overStatement;

    public boolean isShortStyle() {
        return shortStyle;
    }

    public void setShortStyle(boolean shortStyle) {
        this.shortStyle = shortStyle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueListExpression getArgs() {
        return args;
    }

    public void setArgs(ValueListExpression args) {
        this.args = args;
        args.setSpacable(false);
    }

    public OverStatement getOverStatement() {
        return overStatement;
    }

    public void setOverStatement(OverStatement overStatement) {
        this.overStatement = overStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_FUNC_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendWord(name);
        if (args != null && (!args.isEmpty() || !shortStyle)) {
            sqlBuilder.appendExpression(args);
        }
        sqlBuilder.appendExpression(overStatement);
    }
}
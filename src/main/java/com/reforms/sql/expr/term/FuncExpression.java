package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_FUNC_EXPRESSION;

/**
 *
 * @author evgenie
 */
public class FuncExpression extends SelectableExpression {

    private boolean shortStyle;

    private String name;

    private String quantifier;

    private List<SelectableExpression> args = new ArrayList<>();

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

    public String getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
    }

    public List<SelectableExpression> getArgs() {
        return args;
    }

    public boolean addArg(SelectableExpression arg) {
        return args.add(arg);
    }

    public void setArgs(List<SelectableExpression> args) {
        this.args = args;
    }

    @Override
    public ExpressionType getType() {
        return ET_FUNC_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendWord(name);
        if (!args.isEmpty() || !shortStyle) {
            sqlBuilder.append("(");
            if (quantifier != null) {
                sqlBuilder.appendSpace();
                sqlBuilder.appendWord(quantifier);
            }
            sqlBuilder.appendExpressions(args, ",");
            sqlBuilder.append(")");
        }
    }
}
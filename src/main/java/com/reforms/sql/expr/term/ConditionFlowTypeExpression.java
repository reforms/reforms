package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_CONDITION_FLOW_TYPE_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

public class ConditionFlowTypeExpression extends Expression {

    private ConditionFlowType conditionFlowType;

    public ConditionFlowTypeExpression(ConditionFlowType conditionFlowType) {
        this.conditionFlowType = conditionFlowType;
    }

    @Override
    public ExpressionType getType() {
        return ET_CONDITION_FLOW_TYPE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendWord(conditionFlowType.getCondition());
    }

}

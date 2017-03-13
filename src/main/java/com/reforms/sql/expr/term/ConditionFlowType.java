package com.reforms.sql.expr.term;

import static com.reforms.sql.parser.SqlWords.SW_AND;
import static com.reforms.sql.parser.SqlWords.SW_OR;

/**
 *
 * @author evgenie
 */
public enum ConditionFlowType {
    CFT_OR(SW_OR),
    CFT_AND(SW_AND);

    private String condition;

    private ConditionFlowType(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public static ConditionFlowType resolveConditionFlowType(String condition) {
        if (condition == null) {
            return null;
        }
        String preparedCondition = condition.trim().toUpperCase();
        if (CFT_OR.getCondition().equals(preparedCondition)) {
            return CFT_OR;
        }
        if (CFT_AND.getCondition().equals(preparedCondition)) {
            return CFT_AND;
        }
        return null;
    }
}

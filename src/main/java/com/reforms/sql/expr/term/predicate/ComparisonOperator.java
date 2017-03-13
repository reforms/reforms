package com.reforms.sql.expr.term.predicate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evgenie
 */
public enum ComparisonOperator {
    COT_EQUALS("="),
    COT_NOT_EQUALS("<>"),
    COT_JAVA_NOT_EQUALS("!="),
    COT_LESS_THAN("<"),
    COT_GREATER_THAN(">"),
    COT_LESS_THAN_OR_EQUALS("<="),
    COT_GREATER_THAN_OR_EQUALS(">=");

    private static final Map<String, ComparisonOperator> VALUE2OPERATOR = init();

    private static Map<String, ComparisonOperator> init() {
        Map<String, ComparisonOperator> value2operator = new HashMap<>();
        for (ComparisonOperator operator : values()) {
            value2operator.put(operator.getOperator(), operator);
        }
        return Collections.unmodifiableMap(value2operator);
    }

    private final String operator;

    private ComparisonOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static ComparisonOperator resolveComparisonOperatorType(String value) {
        return VALUE2OPERATOR.get(value);
    }
}

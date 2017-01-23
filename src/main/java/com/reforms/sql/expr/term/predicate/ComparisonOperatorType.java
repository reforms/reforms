package com.reforms.sql.expr.term.predicate;

/**
 *
 * @author palihov
 */
public enum ComparisonOperatorType {
    COT_EQUALS("="),
    COT_NOT_EQUALS("<>"),
    COT_JAVA_NOT_EQUALS("!="),
    COT_LESS_THAN("<"),
    COT_GREATER_THAN(">"),
    COT_LESS_THAN_OR_EQUALS("<="),
    COT_GREATER_THAN_OR_EQUALS(">=");

    private final String operator;

    private ComparisonOperatorType(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

}

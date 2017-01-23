package com.reforms.sql.expr.term;

public enum MathOperator {
    MO_PLUS("+"),
    MO_SUB("-"),
    MO_MULT("*"),
    MO_DIV("/"),
    MO_CONCAT("||");

    private final String sign;

    private MathOperator(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public static MathOperator resolveMathOperator(String operatorValue) {
        if (MO_PLUS.getSign().equals(operatorValue)) {
            return MO_PLUS;
        }
        if (MO_SUB.getSign().equals(operatorValue)) {
            return MO_SUB;
        }
        if (MO_MULT.getSign().equals(operatorValue)) {
            return MO_MULT;
        }
        if (MO_DIV.getSign().equals(operatorValue)) {
            return MO_DIV;
        }
        if (MO_CONCAT.getSign().equals(operatorValue)) {
            return MO_CONCAT;
        }
        return null;
    }
}

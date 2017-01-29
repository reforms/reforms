package com.reforms.sql.expr.term.value;

/**
 * Выражение идентичное обычному ? только в дополнение маркерующее владельца - используется при постраничной разбивке
 * @author evgenie
 */
public class PageQuestionExpression extends ValueExpression {

    public static int PQE_LIMIT = 1;

    public static int PQE_OFFSET = 2;

    private int type;

    public PageQuestionExpression(int type) {
        super("?", ValueExpressionType.VET_PAGE_QUESTION);
        this.type = type;
    }

    public boolean isLimitType() {
        return PQE_LIMIT == type;
    }

    public boolean isOffsetType() {
        return PQE_OFFSET == type;
    }

}

package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUES_EXPRESSION;
import static com.reforms.sql.parser.SqlWords.SW_VALUES;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * VALUES (?, ?, ?)
 * @author evgenie
 */
public class ValuesExpression extends Expression {

    private String valuesWord = SW_VALUES;

    private ValueListExpression valueListExpr;

    public String getValuesWord() {
        return valuesWord;
    }

    public void setValuesWord(String valuesWord) {
        this.valuesWord = valuesWord;
    }

    public ValueListExpression getValueListExpr() {
        return valueListExpr;
    }

    public void setValueListExpr(ValueListExpression valueListExpr) {
        this.valueListExpr = valueListExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_VALUES_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(valuesWord);
        sqlBuilder.appendExpression(valueListExpr);
    }

}

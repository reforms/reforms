package com.reforms.sql.expr.query;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.FuncExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_CALL_QUERY;
import static com.reforms.sql.parser.SqlWords.SW_CALL;

/**
 * Хранимые процедуры
 * GRAMMAR: {[? =] call store_procedure_name([args])}
 * @author evgenie
 */
public class CallQuery extends Expression {

    /** Необязательный out параметр*/
    private Expression questionExpr;

    /** CALL keyword */
    private String callWord = SW_CALL;

    /** Сама хранимая процедура */
    private FuncExpression funcExpr;

    public Expression getQuestionExpr() {
        return questionExpr;
    }

    public void setQuestionExpr(Expression questionExpr) {
        this.questionExpr = questionExpr;
    }

    public String getCallWord() {
        return callWord;
    }

    public void setCallWord(String callWord) {
        this.callWord = callWord;
    }

    public FuncExpression getFuncExpr() {
        return funcExpr;
    }

    public void setFuncExpr(FuncExpression funcExpr) {
        this.funcExpr = funcExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_CALL_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace().append("{");
        if (questionExpr != null) {
            sqlBuilder.appendExpression(questionExpr);
            sqlBuilder.appendSpace().append("=").appendSpace();
        }
        sqlBuilder.append(callWord);
        sqlBuilder.appendExpression(funcExpr);
        sqlBuilder.append("}");
    }
}
package com.reforms.sql.expr.query;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.FuncExpression;
import com.reforms.sql.expr.term.ValueListExpression;
import com.reforms.sql.expr.term.value.QuestionExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_CALL_QUERY;
import static com.reforms.sql.parser.SqlWords.SW_CALL;

/**
 * Хранимые процедуры
 * GRAMMAR: {[? =] call store_procedure_name([args])}
 * GRAMMAR: {(value1, value2) call store_procedure_name([args])}
 * @author evgenie
 */
public class CallQuery extends Expression {

    /** Необязательный out параметр*/
    private QuestionExpression questionExpr;

    /**
     * Признак того, что нужно отображать хранимку в JDBC представлении
     */
    private boolean jdbcView = true;

    /**
     * Необязательный out параметр {(id, name) = CALL LOAD_CLIENTS()}
     * valuesExpr -> (id, name)
     */
    private ValueListExpression valuesExpr;

    /** CALL keyword */
    private String callWord = SW_CALL;

    /** Сама хранимая процедура */
    private FuncExpression funcExpr;

    public QuestionExpression getQuestionExpr() {
        return questionExpr;
    }

    public void setQuestionExpr(QuestionExpression questionExpr) {
        this.questionExpr = questionExpr;
    }

    public ValueListExpression getValuesExpr() {
        return valuesExpr;
    }

    public void setValuesExpr(ValueListExpression valuesExpr) {
        this.valuesExpr = valuesExpr;
    }

    public boolean isJdbcView() {
        return jdbcView;
    }

    public void setJdbcView(boolean jdbcView) {
        this.jdbcView = jdbcView;
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

    public boolean hasReturnType() {
        return questionExpr != null || valuesExpr != null;
    }

    @Override
    public ExpressionType getType() {
        return ET_CALL_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace().append("{");
        if (jdbcView) {
            if (questionExpr != null) {
                sqlBuilder.appendExpression(questionExpr);
                sqlBuilder.appendSpace().append("=").appendSpace();
            }
        } else {
            if (valuesExpr != null) {
                sqlBuilder.appendExpression(valuesExpr);
                sqlBuilder.appendSpace().append("=").appendSpace();
            }
        }
        sqlBuilder.append(callWord);
        sqlBuilder.appendExpression(funcExpr);
        sqlBuilder.append("}");
    }
}
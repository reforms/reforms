package com.reforms.sql.expr.term.casee;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_CASE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_CASE;
import static com.reforms.sql.expr.term.SqlWords.SW_END;

/**
 * ------------------------- V1 ---------------------------------
 * Simple CASE expression:
 * CASE input_expression
 *      WHEN when_expression THEN result_expression [ ...n ]
 *      [ ELSE else_result_expression ]
 * END
 * ------------------------- V2 ---------------------------------
 * Searched CASE expression:
 * CASE
 *      WHEN Boolean_expression THEN result_expression [ ...n ]
 *      [ ELSE else_result_expression ]
 * END
 * @author evgenie
 */
public class CaseExpression extends SelectableExpression {

    /** CASE */
    private String caseWord = SW_CASE;

    /** END */
    private String endWord = SW_END;

    private Expression operandExpr;

    private List<WhenThenExpression> whenThenExprs = new ArrayList<>();

    private ElseExpression elseExpr;

    public String getCaseWord() {
        return caseWord;
    }

    public void setCaseWord(String caseWord) {
        this.caseWord = caseWord;
    }

    public String getEndWord() {
        return endWord;
    }

    public void setEndWord(String endWord) {
        this.endWord = endWord;
    }

    public Expression getOperandExpr() {
        return operandExpr;
    }

    public void setOperandExpr(Expression operandExpr) {
        this.operandExpr = operandExpr;
    }

    public List<WhenThenExpression> getWhenThenExprs() {
        return whenThenExprs;
    }

    public boolean addWhenThenExprs(WhenThenExpression whenThenExpr) {
        return whenThenExprs.add(whenThenExpr);
    }

    public void setWhenThenExprs(List<WhenThenExpression> whenThenExprs) {
        this.whenThenExprs = whenThenExprs;
    }

    public ElseExpression getElseExpr() {
        return elseExpr;
    }

    public boolean hasElseExpr() {
        return elseExpr != null;
    }

    public void setElseExpr(ElseExpression elseExpr) {
        this.elseExpr = elseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_CASE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(caseWord);
        if (operandExpr != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendExpression(operandExpr);
        }
        for (Expression whenThenExpr : whenThenExprs) {
            sqlBuilder.appendExpression(whenThenExpr);
        }
        sqlBuilder.appendExpression(elseExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(endWord);
    }
}
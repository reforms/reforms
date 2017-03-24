package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SetClauseExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_SET_CLAUSE_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_SET;

/**
 * SET column1 = value1, column2 = value2
 * GRAMMAR:
 * <set clause list>    ::=   <set clause> [ { <comma> <set clause> } ... ]
 * @author evgenie
 */
public class SetClauseStatement extends Expression {

    private String setWord = SW_SET;

    private List<SetClauseExpression> setClauseList = new ArrayList<>();

    public String getSetWord() {
        return setWord;
    }

    public void setSetWord(String setWord) {
        this.setWord = setWord;
    }

    public List<SetClauseExpression> getSetClauseList() {
        return setClauseList;
    }

    public void addSetClauseExpr(SetClauseExpression setClauseExpr) {
        setClauseList.add(setClauseExpr);
    }

    public void setSetClauseList(List<SetClauseExpression> setClauseList) {
        this.setClauseList = setClauseList;
    }

    public int getExprIndex(Expression expr) {
        return setClauseList.indexOf(expr);
    }

    public boolean removeExpr(int exprIndex) {
        return setClauseList.remove(exprIndex) != null;
    }

    public boolean isEmpty() {
        return setClauseList.isEmpty();
    }

    @Override
    public ExpressionType getType() {
        return ET_SET_CLAUSE_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(setWord);
        sqlBuilder.appendExpressions(setClauseList, ",");
    }
}
package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.Collections;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_SELECT_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_SELECT;

public class SelectStatement extends Expression {

    /** SELECT */
    private String selectWord = SW_SELECT;

    /** ALL | DISTINCT */
    private String modeWord;

    /** maybe TOP for example */
    private Expression customExpr;

    private List<SelectableExpression> selectExps = Collections.emptyList();

    public void setSelectWord(String selectWord) {
        this.selectWord = selectWord;
    }

    public String getSelectWord() {
        return selectWord;
    }

    public String getModeWord() {
        return modeWord;
    }

    public void setModeWord(String modeWord) {
        this.modeWord = modeWord;
    }

    public void setCustomExpr(Expression customExpr) {
        this.customExpr = customExpr;
    }

    public Expression getCustomExpr() {
        return customExpr;
    }

    public void setSelectExps(List<SelectableExpression> selectExps) {
        this.selectExps = selectExps;
    }

    public List<SelectableExpression> getSelectExps() {
        return selectExps;
    }

    @Override
    public ExpressionType getType() {
        return ET_SELECT_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(selectWord);
        if (modeWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(modeWord);
        }
        sqlBuilder.appendExpression(customExpr);
        sqlBuilder.appendExpressions(selectExps, ",");
    }
}

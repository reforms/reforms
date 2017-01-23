package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_SELECT_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_SELECT;

public class SelectStatement extends Expression {

    /** ALL | DISTINCT */
    private String modeWord;

    public String getModeWord() {
        return modeWord;
    }

    public void setModeWord(String modeWord) {
        this.modeWord = modeWord;
    }

    private List<SelectableExpression> selectExps = new ArrayList<>();

    public void setSelectExps(List<SelectableExpression> selectExps) {
        this.selectExps = selectExps;
    }

    public boolean addSelectExpression(SelectableExpression selectExpr) {
        return selectExps.add(selectExpr);
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
        sqlBuilder.appendWord(SW_SELECT);
        if (modeWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(modeWord);
        }
        sqlBuilder.appendExpressions(selectExps, ",");
    }
}

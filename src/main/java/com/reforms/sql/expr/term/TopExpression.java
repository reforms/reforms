package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_TOP_EXPRESSION;
import static com.reforms.sql.parser.SqlWords.SW_TOP;

/**
 * TOP (expression) [PERCENT] [ WITH TIES ]
 * @author evgenie
 */
public class TopExpression extends Expression {

    /** TOP */
    private String topWord = SW_TOP;

    /** wrap expression */
    private boolean argFlag;

    private Expression expression;

    /** PERCENT */
    private String percentWord;

    /** WITH TIES */
    private String withTiesWords;

    public String getTopWord() {
        return topWord;
    }

    public void setTopWord(String topWord) {
        this.topWord = topWord;
    }

    public void setArgFlag(boolean argFlag) {
        this.argFlag = argFlag;
    }

    public boolean isArgFlag() {
        return argFlag;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getPercentWord() {
        return percentWord;
    }

    public void setPercentWord(String percentWord) {
        this.percentWord = percentWord;
    }

    public String getWithTiesWords() {
        return withTiesWords;
    }

    public void setWithTiesWords(String withTiesWords) {
        this.withTiesWords = withTiesWords;
    }

    @Override
    public ExpressionType getType() {
        return ET_TOP_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(topWord);
        if (argFlag) {
            sqlBuilder.append("(");
        }
        sqlBuilder.appendExpression(expression);
        if (argFlag) {
            sqlBuilder.append(")");
        }
        if (percentWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(percentWord);
        }
        if (withTiesWords != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(withTiesWords);
        }
    }
}

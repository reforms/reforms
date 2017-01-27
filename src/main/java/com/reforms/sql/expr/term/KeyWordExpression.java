package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_KEY_WORD_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Example: 'SELECT', 'FROM', 'WHERE'...
 * @author evgenie
 */
public class KeyWordExpression extends Expression {

    private String keyWord;

    public KeyWordExpression(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    @Override
    public ExpressionType getType() {
        return ET_KEY_WORD_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(keyWord);
    }
}

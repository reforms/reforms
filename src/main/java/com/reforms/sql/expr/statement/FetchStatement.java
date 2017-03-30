package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_FETCH_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_FETCH;
import static com.reforms.sql.parser.SqlWords.SW_ONLY;

/**
 * GRAMMAR_1:
 * FETCH
 *           [ [ NEXT | PRIOR | FIRST | LAST
 *                     | ABSOLUTE { n | @nvar }
 *                     | RELATIVE { n | @nvar }
 *                ]
 *                FROM
 *           ]
 * { { [ GLOBAL ] cursor_name } | @cursor_variable_name }
 * [ INTO @variable_name [ ,...n ] ]
 *
 * GRAMMAR_2: [ ORDER BY <order_by_list>
                                [ OFFSET <offset_value> { ROW | ROWS }
                                  [ FETCH { FIRST | NEXT } <fetch_value> { ROW | ROWS } ONLY ] ] ]
 *
  * EXAMPLE: FETCH NEXT 10 ROWS ONLY
  *
  * HERE GRAMMAR_2 only.
  *
 * @author evgenie
 */
public class FetchStatement extends Expression {

    private String fetchWord = SW_FETCH;

    /** NEXT | FIRST */
    private String portionWord;

    private Expression valueExpr;

    /** ROW | ROWS */
    private String rowsWord;

    private String onlyWord = SW_ONLY;

    public String getFetchWord() {
        return fetchWord;
    }

    public void setFetchWord(String fetchWord) {
        this.fetchWord = fetchWord;
    }

    public String getPortionWord() {
        return portionWord;
    }

    public void setPortionWord(String portionWord) {
        this.portionWord = portionWord;
    }

    public Expression getValueExpr() {
        return valueExpr;
    }

    public void setValueExpr(Expression valueExpr) {
        this.valueExpr = valueExpr;
    }

    public String getRowsWord() {
        return rowsWord;
    }

    public void setRowsWord(String rowsWord) {
        this.rowsWord = rowsWord;
    }

    public String getOnlyWord() {
        return onlyWord;
    }

    public void setOnlyWord(String onlyWord) {
        this.onlyWord = onlyWord;
    }

    @Override
    public ExpressionType getType() {
        return ET_FETCH_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(fetchWord);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(portionWord);
        sqlBuilder.appendExpression(valueExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(rowsWord);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(onlyWord);
    };
}
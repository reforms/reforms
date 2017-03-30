package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_PARTITION_BY_STATEMENT;
import static com.reforms.sql.parser.SqlWords.SW_BY;
import static com.reforms.sql.parser.SqlWords.SW_PARTITION;

/**
 * GRAMMAR
 * PARTITION BY value_expression , ... [ n ]
 *
 * MSSQL
 * @author evgenie
 */
public class PartitionByStatement extends Expression {

    private String partitionWord = SW_PARTITION;

    private String byWord = SW_BY;

    private List<Expression> valueExprs;

    public String getPartitionWord() {
        return partitionWord;
    }

    public void setPartitionWord(String partitionWord) {
        this.partitionWord = partitionWord;
    }

    public String getByWord() {
        return byWord;
    }

    public void setByWord(String byWord) {
        this.byWord = byWord;
    }

    public List<Expression> getValueExprs() {
        return valueExprs;
    }

    public void setValueExprs(List<Expression> valueExprs) {
        this.valueExprs = valueExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_PARTITION_BY_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(partitionWord);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(byWord);
        sqlBuilder.appendExpressions(valueExprs, ",");
    }
}

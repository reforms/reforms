package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.ColumnAliasParser;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.expr.term.*;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_ALIAS_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_EXPRESSION;

/**
 * TODO логика: подумать над тем, какой символ использовать в качестве краткого алиаса, сейчас используется символ '#'.
 * @author evgenie
 */
@ThreadSafe
class SelectColumnExtractorAndAliasModifier {

    SelectColumnExtractorAndAliasModifier() {
    }

    public List<SelectedColumn> extractSelectedColumns(SelectQuery selectQuery) {
        List<SelectedColumn> columns = new ArrayList<>();
        SelectStatement selectStatement = selectQuery.getSelectStatement();
        List<SelectableExpression> selectableExprs = selectStatement.getSelectExps();
        int index = 1;
        for (SelectableExpression selectableExpr : selectableExprs) {
            ExpressionType eType = selectableExpr.getType();
            if (ET_ALIAS_EXPRESSION == eType) {
                AliasExpression aliasExpr = (AliasExpression) selectableExpr;
                SelectedColumn selectedColumn = fromAliasExpression(index, aliasExpr);
                columns.add(selectedColumn);
            } else if (ET_COLUMN_EXPRESSION == eType) {
                ColumnExpression columnExpr = (ColumnExpression) selectableExpr;
                SelectedColumn selectedColumn = fromColumnExpression(index, columnExpr);
                columns.add(selectedColumn);
            }
            index++;
        }
        return columns;
    }

    protected SelectedColumn fromAliasExpression(int index, AliasExpression aliasExpr) {
        SelectedColumn selectedColumn = new SelectedColumn();
        Expression primaryExpr = aliasExpr.getPrimaryExpr();
        if (primaryExpr instanceof ColumnExpression) {
            ColumnExpression columnExpr = (ColumnExpression) primaryExpr;
            selectedColumn.setPrefixColumnName(columnExpr.getPrefix());
            selectedColumn.setColumnName(columnExpr.getColumnName());
        }
        AsClauseExpression asClauseExpr = aliasExpr.getAsClauseExpr();
        String alias = asClauseExpr.getAlias();
        ColumnAliasParser columnAliasParser = OrmConfigurator.getInstance(ColumnAliasParser.class);
        ColumnAlias cAlias = columnAliasParser.parseColumnAlias(alias);
        // Для краткости можно использовать синтаксис #type
        // Эта часть кода обрабатывает такую ситуацию
        if (cAlias != null) {
            asClauseExpr.setAlias(cAlias.getSqlAliasKey());
        }
        selectedColumn.setIndex(index);
        selectedColumn.setColumnAlias(cAlias);
        return selectedColumn;
    }

    protected SelectedColumn fromColumnExpression(int index, ColumnExpression columnExpr) {
        SelectedColumn selectedColumn = new SelectedColumn();
        String columnName = columnExpr.getColumnName();
        selectedColumn.setIndex(index);
        selectedColumn.setColumnName(columnName);
        selectedColumn.setPrefixColumnName(columnExpr.getPrefix());
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(columnName);
        cAlias.setJavaAliasKey(columnName);
        selectedColumn.setColumnAlias(cAlias);
        return selectedColumn;
    }

}

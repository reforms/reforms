package com.reforms.orm.extractor;

import static com.reforms.sql.expr.term.ExpressionType.ET_ALIAS_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_EXPRESSION;

import java.util.ArrayList;
import java.util.List;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.ColumnAliasParser;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.expr.term.*;

/**
 * TODO логика: подумать над тем, какой символ использовать в качестве краткого алиаса, сейчас используется символ '#'.
 * @author evgenie
 */
public class SelectColumnExtractorAndAliasModifier {

    public List<SelectedColumn> extractSelectedColumns(SelectQuery selectQuery) {
        List<SelectedColumn> columns = new ArrayList<>();
        SelectStatement selectStatement = selectQuery.getSelectStatement();
        List<SelectableExpression> selectableExprs = selectStatement.getSelectExps();
        int index = 1;
        ColumnAliasParser columnAliasParser = new ColumnAliasParser();
        for (SelectableExpression selectableExpr : selectableExprs) {
            ExpressionType eType = selectableExpr.getType();
            if (ET_ALIAS_EXPRESSION == eType) {
                SelectedColumn selectedColumn = new SelectedColumn();
                AliasExpression aliasExpr = (AliasExpression) selectableExpr;
                Expression primaryExpr = aliasExpr.getPrimaryExpr();
                if (primaryExpr instanceof ColumnExpression) {
                    ColumnExpression columnExpr = (ColumnExpression) primaryExpr;
                    selectedColumn.setPrefixColumnName(columnExpr.getPrefix());
                    selectedColumn.setColumnName(columnExpr.getColumnName());
                }
                AsClauseExpression asClauseExpr = aliasExpr.getAsClauseExpr();
                String alias = asClauseExpr.getAlias();
                // Для краткости можно использовать синтаксис #type
                // Эта часть кода обрабатывает такую ситуацию
                if (alias != null && !alias.isEmpty() && '#' == alias.charAt(0)) {
                    String newAlias = null;
                    if (selectedColumn.getColumnName() != null) {
                        newAlias = alias.substring(1) + "_" + selectedColumn.getColumnName();
                    } else {
                        newAlias = "__A__" + index;
                    }
                    asClauseExpr.setAlias(newAlias);
                    alias = newAlias;
                }
                ColumnAlias cAlias = columnAliasParser.parseColumnAlias(alias);
                selectedColumn.setIndex(index);
                selectedColumn.setColumnAlias(cAlias);
                columns.add(selectedColumn);
            } else if (ET_COLUMN_EXPRESSION == eType) {
                SelectedColumn selectedColumn = new SelectedColumn();
                ColumnExpression columnExpr = (ColumnExpression) selectableExpr;
                String columnName = columnExpr.getColumnName();
                selectedColumn.setIndex(index);
                selectedColumn.setColumnName(columnName);
                selectedColumn.setPrefixColumnName(columnExpr.getPrefix());
                ColumnAlias cAlias = columnAliasParser.parseColumnAlias(columnName);
                selectedColumn.setColumnAlias(cAlias);
                columns.add(selectedColumn);
            }
            index++;
        }
        return columns;
    }

}

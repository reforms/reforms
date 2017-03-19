package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.ColumnAliasParser;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.filter.column.AllSelectedColumnFilter;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.expr.term.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.column.ColumnAliasType.CAT_S_STRING;
import static com.reforms.sql.expr.term.ExpressionType.ET_ALIAS_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_EXPRESSION;

/**
 * Изменяет SelectQuery
 * @author evgenie
 */
@ThreadSafe
class SelectColumnExtractorAndAliasModifier {

    SelectColumnExtractorAndAliasModifier() {
    }

    public List<SelectedColumn> extractSelectedColumns(SelectQuery selectQuery) {
        return extractSelectedColumns(selectQuery, null);
    }

    public List<SelectedColumn> extractSelectedColumns(SelectQuery selectQuery, ISelectedColumnFilter selectedColumnFilter) {
        if (selectedColumnFilter == null) {
            selectedColumnFilter = getInstance(AllSelectedColumnFilter.class);
        }
        List<SelectedColumn> columns = new ArrayList<>();
        SelectStatement selectStatement = selectQuery.getSelectStatement();
        List<SelectableExpression> selectableExprs = selectStatement.getSelectExps();
        int index = 1;
        Iterator<SelectableExpression> selectableExprIterator = selectableExprs.iterator();
        while (selectableExprIterator.hasNext()) {
            SelectableExpression selectableExpr = selectableExprIterator.next();
            ExpressionType eType = selectableExpr.getType();
            SelectedColumn selectedColumn = null;
            if (ET_ALIAS_EXPRESSION == eType) {
                AliasExpression aliasExpr = (AliasExpression) selectableExpr;
                selectedColumn = fromAliasExpression(index, aliasExpr);
            } else if (ET_COLUMN_EXPRESSION == eType) {
                ColumnExpression columnExpr = (ColumnExpression) selectableExpr;
                selectedColumn = fromColumnExpression(index, columnExpr);
            } else {
                selectedColumn = fromAnyExpression(index, selectableExpr);
            }
            if (selectedColumnFilter.acceptSelectedColumn(selectedColumn)) {
                columns.add(selectedColumn);
            } else {
                // bad design code
                selectableExprIterator.remove();
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

    /**
     * TODO доработка: Проверить функции по справочнику. Справочник прогрузить заранее всех известных функций и выражений!
     * @param index
     * @param anyExpr
     * @return колонка для выборки
     */
    protected SelectedColumn fromAnyExpression(int index, Expression anyExpr) {
        SelectedColumn selectedColumn = new SelectedColumn();
        selectedColumn.setIndex(index);
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAliasType(CAT_S_STRING);
        selectedColumn.setColumnAlias(cAlias);
        return selectedColumn;
    }

}

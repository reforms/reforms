package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.filter.column.DefaultSelectedColumnFilter;
import com.reforms.orm.dao.filter.column.FilterState;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.sql.expr.query.CallQuery;
import com.reforms.sql.expr.term.ColumnExpression;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.ValueListExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.column.SelectedColumnConst.SCC_NO_NAME_INDEX_1;
import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_EXPRESSION;

/**
 * Изменяет CallQuery и возращает список колонок для выборки
 * @author evgenie
 */
@ThreadSafe
public class SelectColumnCallableExtractor {

    private final IColumnToFieldNameConverter columnToFieldNameConverter;

    public SelectColumnCallableExtractor() {
        columnToFieldNameConverter = getInstance(IColumnToFieldNameConverter.class);
    }

    public List<SelectedColumn> extractSelectedColumns(CallQuery selectQuery) {
        return extractSelectedColumns(selectQuery, null);
    }

    public List<SelectedColumn> extractSelectedColumns(CallQuery callQuery, ISelectedColumnFilter selectedColumnFilter) {
        if (selectedColumnFilter == null) {
            selectedColumnFilter = getInstance(DefaultSelectedColumnFilter.class);
        }
        List<SelectedColumn> columns = new ArrayList<>();
        if (callQuery.getValuesExpr() != null) {
            int index = 1;
            ValueListExpression valueListExpr = callQuery.getValuesExpr();
            Iterator<Expression> selectableExprIterator = valueListExpr.getValueExprs().iterator();
            while (selectableExprIterator.hasNext()) {
                Expression valueExpr = selectableExprIterator.next();
                ExpressionType eType = valueExpr.getType();
                SelectedColumn selectedColumn = null;
                if (ET_COLUMN_EXPRESSION == eType) {
                    ColumnExpression columnExpr = (ColumnExpression) valueExpr;
                    selectedColumn = fromColumnExpression(index, columnExpr);
                }
                if (selectedColumn == null) {
                    throw new IllegalStateException("Указание выражения отличного от ColumnExpression не допускается. Список '"
                            + valueListExpr + "' в '" + valueExpr + "'");
                }
                FilterState filterState = selectedColumnFilter.acceptSelectedColumn(selectedColumn);
                if (filterState == FilterState.FS_ACCEPT) {
                    columns.add(selectedColumn);
                }
                if (filterState == FilterState.FS_REMOVE) {
                    // bad design code
                    selectableExprIterator.remove();
                }
                index++;
            }
        } else if (callQuery.getQuestionExpr() != null) {
            columns.add(SCC_NO_NAME_INDEX_1);
        }
        return columns;
    }

    protected SelectedColumn fromColumnExpression(int index, ColumnExpression columnExpr) {
        SelectedColumn selectedColumn = new SelectedColumn();
        String columnName = columnExpr.getColumnName();
        selectedColumn.setIndex(index);
        selectedColumn.setPrefixColumnName(unwrapValue(columnExpr.getPrefix()));
        String unwrappedColumnName = unwrapValue(columnName);
        selectedColumn.setColumnName(unwrappedColumnName);
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(columnName);
        cAlias.setJavaAliasKey(unwrappedColumnName);
        selectedColumn.setColumnAlias(cAlias);
        ColumnAlias columnAlias = selectedColumn.getColumnAlias();
        String javaFieldName = columnAlias.getJavaAliasKey();
        if (javaFieldName != null) {
            String lowerCaseFieldName = javaFieldName.toLowerCase();
            columnAlias.setJavaAliasKey(lowerCaseFieldName);
        }
        String fieldName = columnToFieldNameConverter.getFieldName(selectedColumn);
        selectedColumn.setFieldName(fieldName);
        return selectedColumn;
    }

    private String unwrapValue(String value) {
        if (value == null) {
            return null;
        }
        if (value.charAt(0) != '"') {
            return value;
        }
        return value.substring(1, value.length() - 1);
    }
}

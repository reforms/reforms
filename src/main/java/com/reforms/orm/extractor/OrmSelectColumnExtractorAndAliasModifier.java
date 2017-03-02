package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.OrmContext;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.IColumnToFieldNameConverter;
import com.reforms.sql.expr.term.AliasExpression;
import com.reforms.sql.expr.term.ColumnExpression;

/**
 * Для отчетов нужно указывать тип по умолчанию - строковой
 * @author evgenie
 */
@ThreadSafe
public class OrmSelectColumnExtractorAndAliasModifier extends SelectColumnExtractorAndAliasModifier {

    private IColumnToFieldNameConverter columnToFieldNameConverter;

    public OrmSelectColumnExtractorAndAliasModifier() {
        columnToFieldNameConverter = OrmConfigurator.get(OrmContext.class).getColumnToFieldNameConverter();
    }

    @Override
    protected SelectedColumn fromAliasExpression(int index, AliasExpression aliasExpr) {
        SelectedColumn selectedColumn = super.fromAliasExpression(index, aliasExpr);
        addFieldName(selectedColumn);
        return selectedColumn;
    }

    @Override
    protected SelectedColumn fromColumnExpression(int index, ColumnExpression columnExpr) {
        SelectedColumn selectedColumn = super.fromColumnExpression(index, columnExpr);
        addFieldName(selectedColumn);
        return selectedColumn;
    }

    private void addFieldName(SelectedColumn selectedColumn) {
        String fieldName = columnToFieldNameConverter.getFieldName(selectedColumn);
        selectedColumn.setFieldName(fieldName);
    }
}

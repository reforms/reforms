package com.reforms.orm.extractor;

import static com.reforms.orm.OrmConfigurator.getInstance;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.sql.expr.term.ExtendsSelectableExpression;
import com.reforms.sql.expr.term.ColumnExpression;

/**
 * Для орм нужно указывать тип как есть
 * @author evgenie
 */
@ThreadSafe
public class OrmSelectColumnExtractorAndAliasModifier extends SelectColumnExtractorAndAliasModifier {

    private IColumnToFieldNameConverter columnToFieldNameConverter;

    public OrmSelectColumnExtractorAndAliasModifier() {
        columnToFieldNameConverter = getInstance(IColumnToFieldNameConverter.class);
    }

    @Override
    protected SelectedColumn fromAliasExpression(int index, ExtendsSelectableExpression aliasExpr) {
        SelectedColumn selectedColumn = super.fromAliasExpression(index, aliasExpr);
        addFieldName(selectedColumn);
        return selectedColumn;
    }

    @Override
    protected SelectedColumn fromColumnExpression(int index, ColumnExpression columnExpr) {
        SelectedColumn selectedColumn = super.fromColumnExpression(index, columnExpr);
        ColumnAlias columnAlias = selectedColumn.getColumnAlias();
        String javaFieldName = columnAlias.getJavaAliasKey();
        if (javaFieldName != null) {
            String lowerCaseFieldName = javaFieldName.toLowerCase();
            columnAlias.setJavaAliasKey(lowerCaseFieldName);
        }
        addFieldName(selectedColumn);
        return selectedColumn;
    }

    private void addFieldName(SelectedColumn selectedColumn) {
        String fieldName = columnToFieldNameConverter.getFieldName(selectedColumn);
        selectedColumn.setFieldName(fieldName);
    }
}

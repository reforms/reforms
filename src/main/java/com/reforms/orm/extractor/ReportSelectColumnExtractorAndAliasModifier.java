package com.reforms.orm.extractor;

import static com.reforms.orm.select.ColumnAliasType.CAT_S_STRING;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.sql.expr.term.AliasExpression;
import com.reforms.sql.expr.term.ColumnExpression;

/**
 * Для отчетов нужно указывать тип по умолчанию - строковой
 * @author evgenie
 */
public class ReportSelectColumnExtractorAndAliasModifier extends SelectColumnExtractorAndAliasModifier {

    @Override
    protected SelectedColumn fromAliasExpression(int index, AliasExpression aliasExpr) {
        SelectedColumn selectedColumn = super.fromAliasExpression(index, aliasExpr);
        addDefaultStringType(selectedColumn.getColumnAlias());
        return selectedColumn;
    }

    @Override
    protected SelectedColumn fromColumnExpression(int index, ColumnExpression columnExpr) {
        SelectedColumn selectedColumn = super.fromColumnExpression(index, columnExpr);
        addDefaultStringType(selectedColumn.getColumnAlias());
        return selectedColumn;
    }

    private void addDefaultStringType(ColumnAlias cAlias) {
        if (cAlias != null && cAlias.getAliasType() == null) {
            cAlias.setAliasType(CAT_S_STRING);
            cAlias.setAliasPrefix(CAT_S_STRING.getMarker());
        }
    }
}

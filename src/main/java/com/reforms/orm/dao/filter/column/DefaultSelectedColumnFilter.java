package com.reforms.orm.dao.filter.column;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Фильтрует все колонки с алиасом ~.
 * Это может понадобиться в запросах, когда требуется выбирать колонку для фильтрации, но без необходимости ее выборки.
 * Пример такой ситуации:
 *  SELECT * FROM (SELECT c1, c2, ROWNUM RN:! FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?
 * @author evgenie
 */
@ThreadSafe
public class DefaultSelectedColumnFilter implements ISelectedColumnFilter {

    public static final ISelectedColumnFilter DEFAULT_COLUMNS_FILTER = new DefaultSelectedColumnFilter();

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        ColumnAlias cAlias = selectedColumn.getColumnAlias();
        if (cAlias != null) {
            String fieldName = cAlias.getJavaAliasKey();
            if ("!".equals(fieldName)) {
                return false;
            }
        }
        return true;
    }
}
package com.reforms.orm.dao.column;

import java.util.Collections;
import java.util.List;

public class SelectedColumnConst {

    /** Колонка */
    public static SelectedColumn SCC_NO_NAME_INDEX_1 = noName();

    /** Список колонок 1 штука */
    public static List<SelectedColumn> SCC_SINGLE_COLUMN = Collections.singletonList(SCC_NO_NAME_INDEX_1);

    private SelectedColumnConst() {
    }

    private static SelectedColumn noName() {
        SelectedColumn selectedColumn = new SelectedColumn();
        String columnName = "__no_name__";
        selectedColumn.setIndex(1);
        selectedColumn.setPrefixColumnName(null);
        selectedColumn.setColumnName(columnName);
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(columnName);
        cAlias.setJavaAliasKey(columnName);
        selectedColumn.setColumnAlias(cAlias);
        return selectedColumn;
    }
}
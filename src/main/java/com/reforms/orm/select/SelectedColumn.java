package com.reforms.orm.select;

/**
 * Контейнер для хранения информации о выбираемых колонках
 * @author evgenie
 */
public class SelectedColumn {

    private int index;

    private String prefixColumnName;

    private String columnName;

    private ColumnAlias columnAlias;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPrefixColumnName() {
        return prefixColumnName;
    }

    public void setPrefixColumnName(String prefixColumnName) {
        this.prefixColumnName = prefixColumnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnAlias getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(ColumnAlias columnAlias) {
        this.columnAlias = columnAlias;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(index).append(":").append(columnName).append(":").append(columnAlias);
        return builder.toString();
    }

}

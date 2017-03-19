package com.reforms.orm.dao.filter.column;

import java.util.ArrayList;
import java.util.List;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Фильтр на основе индексов: Индексы начинаются с 1
 * @author evgenie
 */
public class IndexSelectFilter implements ISelectedColumnFilter {

    private List<Integer> indexes = new ArrayList<>();

    public IndexSelectFilter() {
    }

    public IndexSelectFilter(List<Integer> indexes) {
        this.indexes.addAll(indexes);
    }

    public void addIndex(int index) {
        indexes.add(index);
    }

    public void addIndexes(int ... indexes) {
        for (int index : indexes) {
            addIndex(index);
        }
    }

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        return indexes.contains(selectedColumn.getIndex());
    }
}

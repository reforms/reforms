package com.reforms.orm.dao.filter.column;

import com.reforms.orm.dao.column.SelectedColumn;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.orm.dao.filter.column.FilterState.FS_ACCEPT;
import static com.reforms.orm.dao.filter.column.FilterState.FS_NOT_ACCEPT;;

/**
 * Фильтр на основе индексов: Индексы начинаются с 1
 * TODO вместо List<Integer> indexes = new ArrayList<>() сделать Map объектов index на filterState и доработать логику acceptSelectedColumn
 * @author evgenie
 */
public class IndexSelectFilter implements ISelectedColumnFilter {

    private final FilterState ignoreFilterState;
    private final List<Integer> indexes;

    public IndexSelectFilter() {
        this(FS_NOT_ACCEPT);
    }

    public IndexSelectFilter(FilterState ignoreFilterState) {
        this(ignoreFilterState, new ArrayList<>());
    }

    public IndexSelectFilter(FilterState ignoreFilterState, List<Integer> indexes) {
        this.ignoreFilterState = ignoreFilterState;
        this.indexes = indexes;
    }

    public void addIndex(int index) {
        indexes.add(index);
    }

    public void addIndexes(int ... indexes) {
        for (int index : indexes) {
            addIndex(index);
        }
    }

    /* (non-Javadoc)
     * @see com.reforms.orm.dao.filter.column.ISelectedColumnFilter#acceptSelectedColumn(com.reforms.orm.dao.column.SelectedColumn) */
    @Override
    public FilterState acceptSelectedColumn(SelectedColumn selectedColumn) {
        return indexes.contains(selectedColumn.getIndex()) ? FS_ACCEPT : ignoreFilterState;
    }
}

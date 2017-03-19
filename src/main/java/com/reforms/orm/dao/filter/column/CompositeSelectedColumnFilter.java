package com.reforms.orm.dao.filter.column;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Фильтр на основе двух других
 * @author evgenie
 */
public class CompositeSelectedColumnFilter implements ISelectedColumnFilter {

    private final ISelectedColumnFilter firstFilter;

    private final ISelectedColumnFilter secondFilter;

    public CompositeSelectedColumnFilter(ISelectedColumnFilter firstFilter, ISelectedColumnFilter secondFilter) {
        this.firstFilter = firstFilter;
        this.secondFilter = secondFilter;
    }

    public ISelectedColumnFilter getFirstFilter() {
        return firstFilter;
    }

    public ISelectedColumnFilter getSecondFilter() {
        return secondFilter;
    }

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        return (firstFilter != null && firstFilter.acceptSelectedColumn(selectedColumn)) ||
                (secondFilter != null && secondFilter.acceptSelectedColumn(selectedColumn));
    }
}

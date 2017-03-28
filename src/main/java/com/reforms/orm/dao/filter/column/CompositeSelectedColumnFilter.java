package com.reforms.orm.dao.filter.column;

import com.reforms.orm.dao.column.SelectedColumn;

import static com.reforms.orm.dao.filter.column.FilterState.FS_REMOVE;

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
    public FilterState acceptSelectedColumn(SelectedColumn selectedColumn) {
        if (firstFilter != null && secondFilter != null) {
            FilterState firstState = firstFilter.acceptSelectedColumn(selectedColumn);
            FilterState secondState = secondFilter.acceptSelectedColumn(selectedColumn);
            return firstState.getPrior() < secondState.getPrior() ? firstState : secondState;
        }
        if (firstFilter != null) {
            return firstFilter.acceptSelectedColumn(selectedColumn);
        }
        if (secondFilter != null) {
            return secondFilter.acceptSelectedColumn(selectedColumn);
        }
        return FS_REMOVE;
    }
}

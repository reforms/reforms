package com.reforms.orm.dao.filter;

import com.reforms.orm.dao.paging.IPageFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Фильтр в виде последовательного набора значений
 * @author evgenie
 */
public class FilterSequence extends IFilterValues {

    private int pageIndex;

    private IPageFilter pageFilter;

    private Object[] sequenses;

    private Map<String, Integer> filterNames = new HashMap<>();

    public FilterSequence(Object ... sequenses) {
        this.sequenses = sequenses;
        pageIndex = -1;
        for (int index = 0; index < sequenses.length; index++) {
            Object value = sequenses[index];
            if (value instanceof IPageFilter) {
                pageFilter = (IPageFilter) value;
                pageIndex = index;
            }
        }
    }

    @Override
    public Object get(String key) {
        if (pageIndex == filterNames.size()) {
            filterNames.put("__PAGE&INDEX__", pageIndex);
        }
        Integer keyIndex = filterNames.get(key);
        if (keyIndex == null) {
            keyIndex = filterNames.size();
            filterNames.put(key, keyIndex);
        }
        if (keyIndex >= sequenses.length) {
            return null;
        }
        return sequenses[keyIndex];
    }

    @Override
    public Object get(int key) {
        return get(String.valueOf(key));
    }

    @Override
    public IPageFilter getPageFilter() {
        return pageFilter;
    }

    @Override
    public boolean isEmpty() {
        return sequenses.length == 0;
    }

    @Override
    public int getParamNameType() {
        return PNT_SEQUENCE;
    }
}

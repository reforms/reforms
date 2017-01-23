package com.reforms.orm.filter;

import java.util.HashMap;
import java.util.Map;

public class FilterSequence implements FilterValues {

    private int pageIndex;
    private PageFilter pageFilter;
    private Object[] sequenses;

    private Map<String, Integer> filterNames = new HashMap<>();

    public FilterSequence(Object... sequenses) {
        this.sequenses = sequenses;
        pageIndex = -1;
        for (int index = 0; index < sequenses.length; index++) {
            Object value = sequenses[index];
            if (value instanceof PageFilter) {
                pageFilter = (PageFilter) value;
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
    public boolean hasPageFilter() {
        return pageIndex != -1 && pageFilter != null && pageFilter.hasPageFilter();
    }

    @Override
    public Integer getPageLimit() {
        return pageFilter.getPageLimit();
    }

    @Override
    public Integer getPageOffset() {
        return pageFilter.getPageOffset();
    }

}

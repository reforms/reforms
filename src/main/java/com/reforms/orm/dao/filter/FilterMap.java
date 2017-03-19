package com.reforms.orm.dao.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.reforms.orm.dao.filter.page.IPageFilter;

/**
 * Вспомогательный класс для фильтров
 * TODO fitch: добавить регистро-независимый поиск в мапе
 * TODO fitch: сделать добавление параметров через типизированные методы,
 *              например, putLongValue(String key, Long longValue)
 * @author evgenie
 */
public class FilterMap implements IFilterValues {

    public static final String PAGE_LIMIT_KEY = "__PAGE_LIMIT__";
    public static final String PAGE_OFFSET_KEY = "__PAGE_OFFSET__";

    /**
     * TODO не полноценный фильтр - нужно сделать не изменяемым.
     */
    public static final FilterMap EMPTY_FILTER_MAP = new FilterMap();

    private Map<String, Object> filters = Collections.emptyMap();

    public FilterMap() {
    }

    public FilterMap(Map<String, Object> filters) {
        this.filters = filters;
    }

    public FilterMap(String key, Object value) {
        put(key, value);
    }

    public FilterMap(String key1, Object value1, String key2, Object value2) {
        this(key1, value1);
        put(key2, value2);
    }

    public FilterMap(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        this(key1, value1, key2, value2);
        put(key3, value3);
    }

    public FilterMap(int key, Object value) {
        putValue(key, value);
    }

    public FilterMap(int key1, Object value1, int key2, Object value2) {
        this(key1, value1);
        putValue(key2, value2);
    }

    public FilterMap(int key1, Object value1, int key2, Object value2, int key3, Object value3) {
        this(key1, value1, key2, value2);
        putValue(key3, value3);
    }

    @Override
    public Object get(String key) {
        return filters.get(key);
    }

    @Override
    public Object get(int key) {
        return get(String.valueOf(key));
    }

    public FilterMap putValue(String key, Object value) {
        put(key, value);
        return this;
    }

    public FilterMap putValue(int key, Object value) {
        return putValue(String.valueOf(key), value);
    }

    public FilterMap putValues(Map<String, Object> filterPairs) {
        putAll(filterPairs);
        return this;
    }

    public Object getValue(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    public Object getValue(String key) {
        return getValue(key, null);
    }

    private void put(String key, Object value) {
        if (filters == Collections.EMPTY_MAP) {
            filters = new HashMap<>();
        }
        filters.put(key, value);
    }

    private void putAll(Map<String, Object> filterPairs) {
        if (filters == Collections.EMPTY_MAP) {
            filters = new HashMap<>();
        }
        filters.putAll(filterPairs);
    }

    @Override
    public boolean hasPageFilter() {
        return getPageLimit() != null && getPageOffset() != null;
    }

    @Override
    public Integer getPageLimit() {
        Object pageLimit = getValue(PAGE_LIMIT_KEY, null);
        if (pageLimit instanceof Integer) {
            return (Integer) pageLimit;
        }
        return null;
    }

    public void setPageLimit(int pageLimit) {
        putValue(PAGE_LIMIT_KEY, pageLimit);
    }

    @Override
    public void applyPageFilter(IPageFilter newPageFiler) {
        if (newPageFiler != null) {
            if (newPageFiler.getPageLimit() != null) {
                setPageLimit(newPageFiler.getPageLimit());
            }
            if (newPageFiler.getPageOffset() != null) {
                setPageOffset(newPageFiler.getPageOffset());
            }
        }
    }

    @Override
    public Integer getPageOffset() {
        Object pageOffset = getValue(PAGE_OFFSET_KEY, null);
        if (pageOffset instanceof Integer) {
            return (Integer) pageOffset;
        }
        return null;
    }

    public void setPageOffset(int pageOffset) {
        putValue(PAGE_OFFSET_KEY, pageOffset);
    }
}
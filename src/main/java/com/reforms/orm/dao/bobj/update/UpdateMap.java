package com.reforms.orm.dao.bobj.update;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Вспомогательный класс для фильтров
 * TODO fitch: добавить регистро-независимый поиск в мапе
 * TODO fitch: сделать добавление параметров через типизированные методы,
 *              например, putLongValue(String key, Long longValue)
 * @author evgenie
 */
public class UpdateMap extends IUpdateValues {

    /**
     * TODO не полноценный фильтр - нужно сделать не изменяемым.
     */
    public static final UpdateMap EMPTY_VALUES_MAP = new UpdateMap();

    private Map<String, Object> values = Collections.emptyMap();

    public UpdateMap() {
    }

    public UpdateMap(Map<String, Object> values) {
        this.values = values;
    }

    public UpdateMap(String key, Object value) {
        put(key, value);
    }

    public UpdateMap(String key1, Object value1, String key2, Object value2) {
        this(key1, value1);
        put(key2, value2);
    }

    public UpdateMap(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        this(key1, value1, key2, value2);
        put(key3, value3);
    }

    public UpdateMap(int key, Object value) {
        putValue(key, value);
    }

    public UpdateMap(int key1, Object value1, int key2, Object value2) {
        this(key1, value1);
        putValue(key2, value2);
    }

    public UpdateMap(int key1, Object value1, int key2, Object value2, int key3, Object value3) {
        this(key1, value1, key2, value2);
        putValue(key3, value3);
    }

    @Override
    public Object get(String key) {
        return values.get(key);
    }

    @Override
    public Object get(int key) {
        return get(String.valueOf(key));
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public int getParamNameType() {
        return PNT_MAP;
    }

    public UpdateMap putValue(String key, Object value) {
        put(key, value);
        return this;
    }

    public UpdateMap putValue(int key, Object value) {
        return putValue(String.valueOf(key), value);
    }

    public UpdateMap putValues(Map<String, Object> filterPairs) {
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
        if (values == Collections.EMPTY_MAP) {
            values = new HashMap<>();
        }
        values.put(key, value);
    }

    private void putAll(Map<String, Object> filterPairs) {
        if (values == Collections.EMPTY_MAP) {
            values = new HashMap<>();
        }
        values.putAll(filterPairs);
    }
}
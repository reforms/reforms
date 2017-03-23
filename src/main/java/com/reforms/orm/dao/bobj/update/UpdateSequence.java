package com.reforms.orm.dao.bobj.update;

import java.util.HashMap;
import java.util.Map;

/**
 * Фильтр в виде последовательного набора значений
 * @author evgenie
 */
public class UpdateSequence extends IUpdateValues {

    private Object[] sequenses;

    private Map<String, Integer> filterNames = new HashMap<>();

    public UpdateSequence(Object ... sequenses) {
        this.sequenses = sequenses;
    }

    @Override
    public Object get(String key) {
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
    public boolean isEmpty() {
        return sequenses.length == 0;
    }
}

package com.reforms.orm.dao.proxy;

import com.reforms.orm.dao.bobj.update.IInsertValues;
import com.reforms.orm.dao.bobj.update.UpdateMap;
import com.reforms.orm.dao.bobj.update.UpdateObject;
import com.reforms.orm.dao.bobj.update.UpdateSequence;

import java.util.Iterator;
import java.util.Map;

/**
 * Итератор конвертирующий объекты в IUpdateValues
 * @author evgenie
 */
class InsertValuesIterator implements Iterator<IInsertValues> {

    private final Iterator<Object> values;
    InsertValuesIterator(Iterator<Object> values) {
        this.values = values;
    }

    @Override
    public boolean hasNext() {
        return values.hasNext();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IInsertValues next() {
        if (hasNext()) {
            Object value = values.next();
            if (value instanceof Map) {
                return new UpdateMap((Map) value);
            }
            if (value != null && value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                return new UpdateSequence(values);
            }
            return new UpdateObject(value);
        }
        return null;
    }
}

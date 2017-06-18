package com.reforms.orm.dao.proxy;

import java.util.Iterator;
import java.util.Map;

import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.bobj.update.UpdateMap;
import com.reforms.orm.dao.bobj.update.UpdateObject;
import com.reforms.orm.dao.bobj.update.UpdateSequence;

/**
 * Итератор конвертирующий объекты в IUpdateValues
 * @author evgenie
 */
class UpdateValuesIterator implements Iterator<IUpdateValues> {

    private final Iterator<Object> values;
    UpdateValuesIterator(Iterator<Object> values) {
        this.values = values;
    }

    @Override
    public boolean hasNext() {
        return values.hasNext();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IUpdateValues next() {
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

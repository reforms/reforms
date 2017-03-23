package com.reforms.orm.dao.bobj.update;

import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.reflex.Reflexor;

/**
 * TODO оптимизация и рефакторинг
 * @author evgenie
 */
public class UpdateObject extends IUpdateValues {

    private Object filter;
    private IReflexor reflexor;

    public UpdateObject(Object filter) {
        this.filter = filter;
        reflexor = Reflexor.createReflexor(filter.getClass());
    }

    @Override
    public Object get(String key) {
        return reflexor.getValue(filter, key);
    }

    @Override
    public Object get(int key) {
        // Я думаю, этот функционал не потребуется в таком виде
        return get("value" + key);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
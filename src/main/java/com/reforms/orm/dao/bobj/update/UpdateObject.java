package com.reforms.orm.dao.bobj.update;

import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.reflex.Reflexor;

/**
 * TODO оптимизация и рефакторинг
 * @author evgenie
 */
public class UpdateObject extends IUpdateValues {

    private Object updateBobj;
    private IReflexor reflexor;

    public UpdateObject(Object updateBobj) {
        this.updateBobj = updateBobj;
        reflexor = Reflexor.createReflexor(updateBobj.getClass());
    }

    @Override
    public Object get(String key) {
        return reflexor.getValue(updateBobj, key);
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
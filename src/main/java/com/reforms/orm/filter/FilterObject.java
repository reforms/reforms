package com.reforms.orm.filter;

import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.reflex.Reflexor;

/**
 * TODO оптимизация и рефакторинг
 * @author evgenie
 */
public class FilterObject implements IFilterValues {

    private Object filter;
    private IReflexor reflexor;

    public FilterObject(Object filter) {
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
    public boolean hasPageFilter() {
        return getPageLimit() != null || getPageOffset() != null;
    }

    @Override
    public Integer getPageLimit() {
        if (reflexor.hasKey("pageLimit")) {
            Object pageLimit = reflexor.getValue(filter, "pageLimit");
            if (pageLimit instanceof Integer) {
                return (Integer) pageLimit;
            }
        }
        return null;
    }

    @Override
    public Integer getPageOffset() {
        if (reflexor.hasKey("pageOffset")) {
            Object pageOffset = reflexor.getValue(filter, "pageOffset");
            if (pageOffset instanceof Integer) {
                return (Integer) pageOffset;
            }
        }
        return null;
    }

}
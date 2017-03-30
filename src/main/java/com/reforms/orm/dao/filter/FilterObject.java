package com.reforms.orm.dao.filter;

import com.reforms.orm.dao.paging.IPageFilter;
import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.reflex.Reflexor;

/**
 * TODO оптимизация и рефакторинг
 * @author evgenie
 */
public class FilterObject extends IFilterValues {

    private Object filter;
    private IReflexor reflexor;
    private IPageFilter pageFilter;

    public FilterObject(Object filter) {
        this(filter, null);
    }

    public FilterObject(Object filter, IPageFilter pageFilter) {
        this.filter = filter;
        this.pageFilter = pageFilter;
        if (this.pageFilter == null && filter instanceof IPageFilter) {
            pageFilter = (IPageFilter) filter;
        }
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
    public IPageFilter getPageFilter() {
        return pageFilter;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getParamNameType() {
        return PNT_BOBJ;
    }
}
package com.reforms.orm.dao.adapter;

import com.reforms.orm.filter.IPageFilter;

/**
 * Контракт на добавление параметров постраничной разбивки
 * @author evgenie
 */
public interface IPageFilterAdapter {

    public Object setPageLimit(int pageLimit);

    public Object setPageOffset(int pageOffset);

    public Object setPageOffset(IPageFilter pageFilter);

}

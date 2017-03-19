package com.reforms.orm.dao.adapter;

import com.reforms.orm.dao.filter.page.IPageFilter;

/**
 * Контракт на добавление параметров постраничной разбивки
 * @author evgenie
 */
public interface IPageFilterAdapter<Adapter> {

    Adapter setPageLimit(int pageLimit);

    Adapter setPageOffset(int pageOffset);

    Adapter setPageOffset(IPageFilter pageFilter);

}

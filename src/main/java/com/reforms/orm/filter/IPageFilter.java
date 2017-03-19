package com.reforms.orm.filter;

/**
 * Контракт на получение параметров для постраничной загрузки данных
 * @author evgenie
 */
public interface IPageFilter {

    public boolean hasPageFilter();

    public Integer getPageLimit();

    public Integer getPageOffset();

}

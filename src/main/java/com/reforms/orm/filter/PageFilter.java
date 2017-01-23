package com.reforms.orm.filter;

public interface PageFilter {

    public boolean hasPageFilter();

    public Integer getPageLimit();

    public Integer getPageOffset();

}

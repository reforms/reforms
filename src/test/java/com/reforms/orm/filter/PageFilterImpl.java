package com.reforms.orm.filter;

public class PageFilterImpl implements PageFilter {

    private Integer pageLimit;
    private Integer pageOffset;

    public PageFilterImpl() {
        this(null, null);
    }

    public PageFilterImpl(Integer pageLimit, Integer pageOffset) {
        this.pageLimit = pageLimit;
        this.pageOffset = pageOffset;
    }

    @Override
    public boolean hasPageFilter() {
        return pageLimit != null && pageOffset != null;
    }

    @Override
    public Integer getPageLimit() {
        return pageLimit;
    }

    @Override
    public Integer getPageOffset() {
        return pageOffset;
    }

}

package com.reforms.orm.dao.paging;

/**
 * @author evgenie
 */
public class PageFilter implements IPageFilter {

    private Integer pageLimit;

    private Integer pageOffset;

    public PageFilter() {
        this(null, null);
    }

    public PageFilter(Integer pageLimit, Integer pageOffset) {
        this.pageLimit = pageLimit;
        this.pageOffset = pageOffset;
    }

    @Override
    public boolean hasPageFilter() {
        return pageLimit != null || pageOffset != null;
    }

    @Override
    public Integer getPageLimit() {
        return pageLimit;
    }

    @Override
    public Integer getPageOffset() {
        return pageOffset;
    }

    @Override
    public String toString() {
        return "[pageLimit=" + pageLimit + ", pageOffset=" + pageOffset + "]";
    }
}

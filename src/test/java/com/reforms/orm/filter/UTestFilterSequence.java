package com.reforms.orm.filter;

import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.paging.IPageFilter;
import com.reforms.orm.dao.paging.PageFilter;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class UTestFilterSequence {

    @Test
    public void runTest_FilterSequence() {
        BigDecimal price = new BigDecimal("100.00");
        FilterSequence filters = new FilterSequence(1L, "Тапочки", new PageFilter(25, 50), price);
        assertEquals(1L, filters.get("id"));
        assertEquals("Тапочки", filters.get("name"));
        assertEquals(price, filters.get("price"));
        assertEquals(1L, filters.get("id"));
        assertEquals("Тапочки", filters.get("name"));
        IPageFilter pageFilter = filters.getPageFilter();
        assertTrue(pageFilter.hasPageFilter());
        assertEquals(Integer.valueOf(25), pageFilter.getPageLimit());
        assertEquals(Integer.valueOf(50), pageFilter.getPageOffset());
        assertEquals(price, filters.get("price"));
        assertNull(filters.get("empty"));
    }

}

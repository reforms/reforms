package com.reforms.orm.filter;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.reforms.orm.dao.filter.page.PageFilter;
import com.reforms.orm.dao.filter.param.FilterSequence;

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
        assertTrue(filters.hasPageFilter());
        assertEquals(Integer.valueOf(25), filters.getPageLimit());
        assertEquals(Integer.valueOf(50), filters.getPageOffset());
        assertEquals(price, filters.get("price"));
        assertNull(filters.get("empty"));
    }

}

package com.reforms.orm.dao.proxy;

import static org.junit.Assert.*;

import org.junit.Test;

import com.reforms.orm.OrmDao;

/**
 * Test for DaoProxy
 * @author evgenie
 */
public class UTestDaoProxy {

    @Test
    public void testBaseObjectMethodImpl() {
        Empty empty = OrmDao.createDao(null, Empty.class);
        assertNotNull(empty.toString());
        assertTrue(empty.hashCode() != 0);
        assertTrue(empty.equals(empty));
    }

    @Test
    public void testDefaultMethodImpl() {
        Empty empty = OrmDao.createDao(null, Empty.class);
        assertEquals(1, empty.checkDefault());
    }

    interface Empty {

        default int checkDefault() {
            return 1;
        }
    };
}

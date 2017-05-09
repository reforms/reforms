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
        EmptyDao empty = OrmDao.createDao(null, EmptyDao.class);
        assertNotNull(empty.toString());
        assertTrue(empty.hashCode() != 0);
        assertTrue(empty.equals(empty));
    }

    @Test
    public void testDefaultMethodImpl() {
        EmptyDao empty = OrmDao.createDao(null, EmptyDao.class);
        assertEquals(1, empty.checkDefault());
    }

    interface EmptyDao {

        default int checkDefault() {
            return 1;
        }

    };
}

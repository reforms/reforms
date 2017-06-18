package com.reforms.orm.dao.proxy;

import com.reforms.ann.TargetQuery;
import com.reforms.orm.OrmDao;
import com.reforms.orm.reflex.Mirror;

import org.junit.Test;

import static com.reforms.ann.TargetQuery.*;
import static com.reforms.orm.reflex.PrivateAccess.createAccessor;
import static org.junit.Assert.*;

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

    @Test
    public void testGetQueryType() {
        DaoProxy daoProxy = new DaoProxy(null, EmptyDao.class);
        IDaoProxy accessToDao = createAccessor(daoProxy, IDaoProxy.class);
        TargetQuery targetQuery = Mirror.of(new Object[] {
                "SELECT 1",     // 0
                "(select 1",    // 1
                " select 1",    // 2
                "INSERT 1",     // 3
                "insert 1",     // 4
                "\nINSERT 1",   // 5
                "UPDATE 1",     // 6
                "update 1",     // 7
                "\tupdate\n1",  // 8
                "DELETE 1",     // 9
                "delete 1",     // 10
                "     delete 1" // 11
        }, TargetQuery.class);
        assertEquals(targetQuery.toString(), QT_SELECT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_SELECT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_SELECT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_INSERT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_INSERT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_INSERT, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_UPDATE, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_UPDATE, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_UPDATE, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_DELETE, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_DELETE, accessToDao.getQueryType(targetQuery));
        assertEquals(targetQuery.toString(), QT_DELETE, accessToDao.getQueryType(targetQuery));
    }

    interface EmptyDao {

        default int checkDefault() {
            return 1;
        }

    };

    interface IDaoProxy {
        int getQueryType(TargetQuery targetQuery);
    }
}

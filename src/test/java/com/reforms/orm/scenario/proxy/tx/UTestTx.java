package com.reforms.orm.scenario.proxy.tx;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.*;

public class UTestTx extends TestScenarioDao {

    public UTestTx() {
        super("tx.sql", "UTestTx");
    }

    @Test
    public void test_txActionOk() throws Exception {
        Client client = new Client(1, "Иванов");
        Connection connection = h2ds.getConnection();
        IClientDao clientDao = OrmDao.createDao(connection, IClientDao.class);
        connection.setAutoCommit(true);
        OrmDao.txAction(connection, () -> {
            clientDao.insertClient1(client);
            clientDao.insertClient2(client);
            try {
                assertFalse(connection.getAutoCommit());
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        Client c1 = clientDao.loadClient1(1);
        assertNotNull(c1);
        Client c2 = clientDao.loadClient1(1);
        assertNotNull(c2);
    }

    @Test
    public void test_txActionFail() throws Exception {
        Client client = new Client(1, "Иванов");
        Connection connection = h2ds.getConnection();
        IClientDao clientDao = OrmDao.createDao(connection, IClientDao.class);
        connection.setAutoCommit(true);
        boolean hasError = false;
        try {
            OrmDao.txAction(connection, () -> {
                clientDao.insertClient1(client);
                clientDao.insertClient2(client);
                clientDao.insertClient3(client);
            });
            hasError = false;
        } catch (Exception ex) {
            hasError = true;
        }
        assertTrue(hasError);
        Client c1 = clientDao.loadClient1(1);
        assertNull(c1);
        Client c2 = clientDao.loadClient1(1);
        assertNull(c2);
    }

}

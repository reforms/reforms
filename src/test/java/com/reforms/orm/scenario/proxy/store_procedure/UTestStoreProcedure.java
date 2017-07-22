package com.reforms.orm.scenario.proxy.store_procedure;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestStoreProcedure extends TestScenarioDao {

    public UTestStoreProcedure() {
        super("store_procedure.sql", "UTestStoreProcedure");
    }

    @Test
    public void testGetNextId() throws Exception {
        IStoreProcedureDao sp = OrmDao.createDao(h2ds, IStoreProcedureDao.class);
        Assert.assertEquals(0, sp.getNextId());
    }

    @Test
    public void testGetObject() throws Exception {
        IStoreProcedureDao sp = OrmDao.createDao(h2ds, IStoreProcedureDao.class);
        Assert.assertEquals(1L, sp.getObject("1"));
        Assert.assertEquals(0L, sp.getObject("2"));
    }

    @Test
    public void testLoadClient() throws Exception {
        IStoreProcedureDao sp = OrmDao.createDao(h2ds, IStoreProcedureDao.class);
        ClientOrm client = sp.getClient();
        Assert.assertEquals(1, client.getId());
        Assert.assertEquals("first client", client.getName());
    }
    @Test
    public void testLoadClients() throws Exception {
        IStoreProcedureDao sp = OrmDao.createDao(h2ds, IStoreProcedureDao.class);
        List<ClientOrm> clients = sp.getClients();
        ClientOrm client1 = clients.get(0);
        Assert.assertEquals(1, client1.getId());
        Assert.assertEquals("first client", client1.getName());
        ClientOrm client2 = clients.get(1);
        Assert.assertEquals(2, client2.getId());
        Assert.assertEquals("second client", client2.getName());
    }

    @Test
    public void liveTest() throws Exception {
        // https://www.mkyong.com/jdbc/jdbc-callablestatement-stored-procedure-cursor-example/
        // https://jdbc.postgresql.org/documentation/81/callproc.html
//        try (CallableStatement cs = h2ds.getConnection().prepareCall("{? = call LOAD_CLIENT()}")) {
//            cs.registerOutParameter(1, Types.REF_CURSOR);
//            if (cs.execute()) {
//                System.out.println(cs.getObject(1));
//            }
//            try (ResultSet cursor = cs.executeQuery()) {
//                while (cursor.next()) {
//                    System.out.println(cursor.getInt(1) + " - " + cursor.getString(2));
//                }
//            }
//        }

    }


}
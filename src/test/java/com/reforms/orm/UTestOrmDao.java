package com.reforms.orm;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.reforms.orm.dao.ReportRecordHandler;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.filter.param.FilterMap;
import com.reforms.orm.dao.filter.param.FilterSequence;
import com.reforms.orm.dao.filter.param.IFilterValues;
import com.reforms.orm.dao.report.model.ReportRecord;

public class UTestOrmDao extends GoodsDbTest {

    public UTestOrmDao() {
        super("UTestOrmDao");
    }

    private static final GoodsOrm GOODS_1 = initGoods(1, "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
    private static final GoodsOrm GOODS_2 = initGoods(2, "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
    private static final GoodsOrm GOODS_3 = initGoods(3, "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");

    private static GoodsOrm initGoods(int id, String name, String description, String price, String articul, String actTime) {
        GoodsOrm goodsOrm = new GoodsOrm();
        goodsOrm.setId(id);
        goodsOrm.setName(name);
        goodsOrm.setDescription(description);
        goodsOrm.setPrice(new BigDecimal(price));
        goodsOrm.setArticul(articul);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTimeDate = sdf.parse(actTime, new ParsePosition(0));
        goodsOrm.setActTime(actTimeDate);
        return goodsOrm;
    }

    private static final String SELECT_GOODS_QUERY =
            "SELECT id, name, description, price, articul, act_time " +
            "    FROM goods" +
            "        WHERE id = :id";

    @Test
    public void runOrmDao_FilterMap() throws Exception {
        loadAndAssertOrm(SELECT_GOODS_QUERY, new FilterMap("id", 1), GOODS_1);
    }

    private static final String SELECT_GOODS_CLEAR_QUERY =
            "SELECT id, name, description, price, articul, act_time " +
            "    FROM goods" +
            "        WHERE id = ?";

    @Test
    public void runOrmDao_FilterSequense() throws Exception {
        loadAndAssertOrm(SELECT_GOODS_CLEAR_QUERY, new FilterSequence(1L), GOODS_1);
    }

    private static final String SELECT_GOODS_ALL_QUERY =
            "SELECT id, name, description, price, articul, act_time " +
            "    FROM goods" +
            "        WHERE id <> ?";
    @Test
    public void runOrmDao_Handle() throws Exception {
        OrmDao ormDao = new OrmDao(h2ds);
        ormDao.handleOrms(GoodsOrm.class, SELECT_GOODS_ALL_QUERY, new GoodsOrmHandler(), new FilterSequence(-1L));
    }

    @Test
    public void runOrmDao_SimpleHandle() throws Exception {
        OrmDao ormDao = new OrmDao(h2ds);
        ormDao.handleSimpleOrms(GoodsOrm.class, SELECT_GOODS_ALL_QUERY, new GoodsOrmHandler(), -1L);
    }

    private class GoodsOrmHandler implements OrmHandler<GoodsOrm> {
        int index = 0;

        @Override
        public void startHandle() {
            index = 0;
        }

        @Override
        public boolean handleOrm(GoodsOrm actualOrm) {
            index++;
            if (index == 1) {
                assertOrm(GOODS_1, actualOrm);
            } else if (index == 2) {
                assertOrm(GOODS_2, actualOrm);
            } else if (index == 3) {
                assertOrm(GOODS_3, actualOrm);
            } else {
                fail("Не допустимый индекс");
            }
            return true;
        }

        @Override
        public void endHandle() {
            assertEquals(3, index);
        }
    }

    @Test
    public void runTestOrmDao_loadOrmIterator() throws Exception {
        OrmDao ormDao = new OrmDao(h2ds);
        try (OrmIterator<GoodsOrm> loader = ormDao.loadOrmIterator(GoodsOrm.class, SELECT_GOODS_ALL_QUERY, new FilterSequence(-1L))) {
            assertTrue(loader.hasNext());
            assertOrm(GOODS_1, loader.next());
            assertTrue(loader.hasNext());
            assertOrm(GOODS_2, loader.next());
            assertTrue(loader.hasNext());
            assertOrm(GOODS_3, loader.next());
            assertFalse(loader.hasNext());
        }
    }

    private void loadAndAssertOrm(String query, IFilterValues filters, GoodsOrm... expectedGoodsOrms) throws Exception {
        OrmDao ormDao = new OrmDao(h2ds);
        List<GoodsOrm> actualGoodsOrms = ormDao.loadOrms(GoodsOrm.class, query, filters);
        assertEquals(expectedGoodsOrms.length, actualGoodsOrms.size());
        for (int index = 0; index < expectedGoodsOrms.length; index++) {
            GoodsOrm expectedGoodsOrm = expectedGoodsOrms[index];
            GoodsOrm actualGoodsOrm = actualGoodsOrms.get(index);
            assertOrm(expectedGoodsOrm, actualGoodsOrm);
        }
    }

    private void assertOrm(GoodsOrm expectedOrm, GoodsOrm actualOrm) {
        assertEquals(expectedOrm.getId(), expectedOrm.getId());
        assertEquals(expectedOrm.getName(), expectedOrm.getName());
        assertEquals(expectedOrm.getDescription(), expectedOrm.getDescription());
        assertEquals(expectedOrm.getPrice(), expectedOrm.getPrice());
        assertEquals(expectedOrm.getArticul(), expectedOrm.getArticul());
        assertEquals(expectedOrm.getActTime(), expectedOrm.getActTime());
    }

}

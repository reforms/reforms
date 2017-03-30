package com.reforms.orm;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.paging.PageFilter;

public class UTestPageFilterOrmDao extends GoodsDbTest {

    public UTestPageFilterOrmDao() {
        super("UTestPageFilterOrmDao");
    }

    private static final String SELECT_GOODS_QUERY =
            "SELECT id, name, description, price, articul, act_time " +
            "    FROM goods" +
            "        WHERE id <> -1 ORDER BY id ASC";

    @Test
    public void runPageOrmDao_FilterMap() throws Exception {
        FilterMap filters = new FilterMap();
        filters.setPageLimit(1);
        filters.setPageOffset(0);
        loadAndAssertOrm(SELECT_GOODS_QUERY, filters, GOODS_1);

        filters = new FilterMap();
        filters.setPageLimit(2);
        filters.setPageOffset(0);
        loadAndAssertOrm(SELECT_GOODS_QUERY, filters, GOODS_1, GOODS_2);

        filters = new FilterMap();
        filters.setPageLimit(3);
        filters.setPageOffset(0);
        loadAndAssertOrm(SELECT_GOODS_QUERY, filters, GOODS_1, GOODS_2, GOODS_3);

        filters = new FilterMap();
        filters.setPageLimit(1);
        filters.setPageOffset(2);
        loadAndAssertOrm(SELECT_GOODS_QUERY, filters, GOODS_3);
    }

    private static final String SELECT_GOODS_QUERY_2 =
            "SELECT id, name, description, price, articul, act_time " +
            "    FROM goods" +
            "        WHERE id <> -1 ORDER BY id ASC";

    @Test
    public void runPageOrmDao_FilterSequence() throws Exception {
        PageFilter pageFilter = new PageFilter(1, 0);
        loadAndAssertOrm(SELECT_GOODS_QUERY_2, new FilterSequence(pageFilter), GOODS_1);

        pageFilter = new PageFilter(2, 0);
        loadAndAssertOrm(SELECT_GOODS_QUERY_2, new FilterSequence(pageFilter), GOODS_1, GOODS_2);

        pageFilter = new PageFilter(3, 0);
        loadAndAssertOrm(SELECT_GOODS_QUERY_2, new FilterSequence(pageFilter), GOODS_1, GOODS_2, GOODS_3);

        pageFilter = new PageFilter(1, 2);
        loadAndAssertOrm(SELECT_GOODS_QUERY_2, new FilterSequence(pageFilter), GOODS_3);
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

    private void loadAndAssertOrm(String query, IFilterValues filters, GoodsOrm... expectedGoodsOrms) throws Exception {
        OrmDao ormDao = new OrmDao(h2ds);
        List<GoodsOrm> actualGoodsOrms = ormDao.selectOrms(GoodsOrm.class, query, filters);
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

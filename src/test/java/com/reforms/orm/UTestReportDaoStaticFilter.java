package com.reforms.orm;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.reforms.orm.dao.ReportIterator;
import com.reforms.orm.dao.ReportRecordHandler;
import com.reforms.orm.dao.filter.param.FilterMap;
import com.reforms.orm.dao.filter.param.FilterObject;
import com.reforms.orm.dao.filter.param.IFilterValues;
import com.reforms.orm.dao.report.model.Report;
import com.reforms.orm.dao.report.model.ReportRecord;

/**
 * TODO доработка - добавить тесты на is null/ is not null
 * @author evgenie
 *
 */
public class UTestReportDaoStaticFilter extends GoodsDbTest {

    public UTestReportDaoStaticFilter() {
        super("UTestReportDaoStaticFilter");
    }

    private static final String SELECT_GOODS_FULL_QUERY =
            "SELECT id AS l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME "
                    +
                    "    FROM goods WHERE id = :id AND name = :name AND price = :price AND act_time >= :t#act_time_after AND act_time <= :t#act_time_before";

    @Test
    public void runTestReportDaoStaticMapFilter_full_loadReport() throws Exception {
        FilterMap filters = new FilterMap();
        filters.putValue("id", 1L);
        filters.putValue("name", "Тапочки");
        filters.putValue("price", new BigDecimal("100.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTimeAfter = sdf.parse("01.01.2017 19:12:01.689");
        Date actTimeBefore = sdf.parse("01.01.2017 19:12:01.691");
        filters.putValue("act_time_after", actTimeAfter);
        filters.putValue("act_time_before", actTimeBefore);
        assertReport(SELECT_GOODS_FULL_QUERY, filters);
    }

    private static final String SELECT_GOODS_QUERY =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id = :id AND name = :name AND price = :price AND act_time = :t#actTime";

    @Test
    public void runTestReportDaoStaticObjectFilter_full_loadReport() throws Exception {
        GoodsFilter filters = new GoodsFilter();
        filters.setId(1L);
        filters.setName("Тапочки");
        filters.setPrice(new BigDecimal("100.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTime = sdf.parse("01.01.2017 19:12:01.690");
        filters.setActTime(actTime);
        assertReport(SELECT_GOODS_QUERY, new FilterObject(filters));
    }

    private static final String SELECT_GOODS_SIMPLE_QUERY =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id = :id AND name = :name AND price = :price";

    @Test
    public void runTestReportDaoStaticSimpleFilter_full_loadReport() throws Exception {
        assertSimpleReport(SELECT_GOODS_SIMPLE_QUERY, 1L, "Тапочки", new BigDecimal("100.00"));
    }

    private static final String SELECT_GOODS_QUERY_WITH_INNER_FILTER =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id = :goods.id AND name = :goods.name AND price = :goods.price AND act_time = :t#goods.actTime";

    @Test
    public void runTestReportDaoStaticObjectInnerFilter_full_loadReport() throws Exception {
        final GoodsFilter innerFilter = new GoodsFilter();
        innerFilter.setId(1L);
        innerFilter.setName("Тапочки");
        innerFilter.setPrice(new BigDecimal("100.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTime = sdf.parse("01.01.2017 19:12:01.690");
        innerFilter.setActTime(actTime);
        Object rootFilter = new Object() {
            private GoodsFilter goods = innerFilter;
        };
        assertReport(SELECT_GOODS_QUERY_WITH_INNER_FILTER, new FilterObject(rootFilter));
    }

    @Test
    public void runTestReportDaoStaticFilter_loadReportIterator() throws Exception {
        ReportDao reportDao = new ReportDao(h2ds);
        FilterMap filters = new FilterMap();
        filters.putValue("id", 2L);
        filters.putValue("name", "Подушки");
        filters.putValue("price", new BigDecimal("200.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTimeAfter = sdf.parse("02.01.2017 19:13:01.689");
        Date actTimeBefore = sdf.parse("02.01.2017 19:13:01.691");
        filters.putValue("act_time_after", actTimeAfter);
        filters.putValue("act_time_before", actTimeBefore);
        try (ReportIterator reportIterator = reportDao.loadReportIterator(SELECT_GOODS_FULL_QUERY, filters)) {
            assertTrue(reportIterator.hasNext());
            assertReportRecord(reportIterator.next(), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
            assertFalse(reportIterator.hasNext());
        }
    }

    @Test
    public void runTestReportDaoStaticFilter_handleReport() throws Exception {
        ReportDao reportDao = new ReportDao(h2ds);
        FilterMap filters = new FilterMap();
        filters.putValue("id", 3L);
        filters.putValue("name", "Одеяло");
        filters.putValue("price", new BigDecimal("300.00"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date actTimeAfter = sdf.parse("03.01.2017 19:14:01.689");
        Date actTimeBefore = sdf.parse("03.01.2017 19:14:01.691");
        filters.putValue("act_time_after", actTimeAfter);
        filters.putValue("act_time_before", actTimeBefore);
        reportDao.handleReport(SELECT_GOODS_FULL_QUERY, new ReportRecordHandler() {
            int index = 0;

            @Override
            public void startHandle() {
                index = 0;
            }

            @Override
            public boolean handleReportRecord(ReportRecord reportRecord) {
                index++;
                if (index == 1) {
                    assertReportRecord(reportRecord, "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
                } else {
                    fail("Не допустимый индекс");
                }
                return true;
            }

            @Override
            public void endHandle() {
                assertEquals(1, index);
            }
        }, filters);
    }

    private static final String SELECT_GOODS_FULL_QUERY_IN_FILTER =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id IN (:ids)";

    @Test
    public void runTestReportDaoStaticFilter_full_loadReportInFilter() throws Exception {
        FilterMap filters = new FilterMap("ids", Arrays.asList(1L, 2L, 3L));
        assertFullReport(SELECT_GOODS_FULL_QUERY_IN_FILTER, filters);
    }

    private static final String SELECT_GOODS_FULL_QUERY_IN_FILTER_ARRAY =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id IN (:ids)";

    @Test
    public void runTestReportDaoStaticFilter_full_loadReportInFilterArray() throws Exception {
        FilterMap filters = new FilterMap("ids", new long[] { 1L, 2L, 3L });
        assertFullReport(SELECT_GOODS_FULL_QUERY_IN_FILTER_ARRAY, filters);
    }

    private static final String SELECT_GOODS_FULL_QUERY_ANDS =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE (id, name) = (:id, :name)";

    @Test
    public void runTestReportDaoStaticFilter_loadReportAnds() throws Exception {
        FilterMap filters = new FilterMap("id", 1L, "name", "Тапочки");
        assertReport(SELECT_GOODS_FULL_QUERY_ANDS, filters);
    }

    private void assertFullReport(String query, FilterMap filters) throws Exception {
        ReportDao reportDao = new ReportDao(h2ds);
        Report report = reportDao.loadReport(query, filters);
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
        assertReportRecord(report.get(1), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
        assertReportRecord(report.get(2), "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
    }

    private void assertReport(String query, IFilterValues filters) throws Exception {
        ReportDao reportDao = new ReportDao(h2ds);
        Report report = reportDao.loadReport(query, filters);
        assertEquals("Ожижается 1 запись", 1, report.size());
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
    }

    private void assertSimpleReport(String query, Object... filters) throws Exception {
        ReportDao reportDao = new ReportDao(h2ds);
        Report report = reportDao.loadSimpleReport(query, filters);
        assertEquals("Ожижается 1 запись", 1, report.size());
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
    }

    private void assertReportRecord(ReportRecord actualRecord, String id, String name, String description, String price, String articul,
            String actTime) {
        assertEquals(id, actualRecord.get("ID"));
        assertEquals(name, actualRecord.get("NAME"));
        assertEquals(description, actualRecord.get("DESCRIPTION"));
        assertEquals(price, actualRecord.get("PRICE"));
        assertEquals(articul, actualRecord.get("ARTICUL"));
        assertEquals(actTime, actualRecord.get("ACT_TIME"));
    }

}

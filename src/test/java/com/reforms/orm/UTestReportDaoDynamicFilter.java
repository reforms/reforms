package com.reforms.orm;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.reforms.orm.filter.FilterMap;
import com.reforms.orm.filter.FilterObject;
import com.reforms.orm.filter.FilterValues;
import com.reforms.orm.select.report.model.Report;
import com.reforms.orm.select.report.model.ReportRecord;

/**
 * TODO доработка - добавить тесты на is null/ is not null
 * @author evgenie
 *
 */
public class UTestReportDaoDynamicFilter extends GoodsDbTest {

    public UTestReportDaoDynamicFilter() {
        super("UTestReportDaoStaticFilter");
    }

    private static final String SELECT_GOODS_SIMPLE_QUERY =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id = ::id AND name = ::name AND price = ::price";

    @Test
    public void runTestReportDaoStaticSimpleFilter_full_loadReport() throws Exception {
        assertSimpleReport(SELECT_GOODS_SIMPLE_QUERY, 1L, "Тапочки", new BigDecimal("100.00"));
    }

    @Test
    public void runTestReportDaoStaticSimpleFilter_full_loadReport_ALL() throws Exception {
        assertFullSimpleReport(SELECT_GOODS_SIMPLE_QUERY);
    }

    @Test
    public void runTestReportDaoDynamicMapFilter_full_loadReport() throws Exception {
        FilterMap filters = new FilterMap();
        filters.putValue("id", 1L);
        filters.putValue("name", "Тапочки");
        filters.putValue("price", new BigDecimal("100.00"));
        assertReport(SELECT_GOODS_SIMPLE_QUERY, filters);
    }

    @Test
    public void runTestReportDaoDynamicMapFilter_full_loadReport_ALL() throws Exception {
        assertFullReport(SELECT_GOODS_SIMPLE_QUERY, EMPTY_FILTER_MAP);
    }

    @Test
    public void runTestReportDaoDynamicBobjFilter_full_loadReport() throws Exception {
        GoodsFilter filters = new GoodsFilter();
        filters.setId(1L);
        filters.setName("Тапочки");
        filters.setPrice(new BigDecimal("100.00"));
        assertReport(SELECT_GOODS_SIMPLE_QUERY, new FilterObject(filters));
    }

    @Test
    public void runTestReportDaoDynamicBobjFilter_full_loadReport_ALL() throws Exception {
        assertFullReport(SELECT_GOODS_SIMPLE_QUERY, new FilterObject(new GoodsFilter()));
    }

    private void assertFullReport(String query, FilterValues filters) throws Exception {
        ReportDao reportDao = new ReportDao();
        Report report = reportDao.loadReport(h2ds, query, filters);
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
        assertReportRecord(report.get(1), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
        assertReportRecord(report.get(2), "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
    }

    private void assertFullSimpleReport(String query, Object... filters) throws Exception {
        ReportDao reportDao = new ReportDao();
        Report report = reportDao.loadSimpleReport(h2ds, query, filters);
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
        assertReportRecord(report.get(1), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
        assertReportRecord(report.get(2), "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
    }

    private void assertReport(String query, FilterValues filters) throws Exception {
        ReportDao reportDao = new ReportDao();
        Report report = reportDao.loadReport(h2ds, query, filters);
        assertEquals("Ожижается 1 запись", 1, report.size());
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
    }

    private void assertSimpleReport(String query, Object... filters) throws Exception {
        ReportDao reportDao = new ReportDao();
        Report report = reportDao.loadSimpleReport(h2ds, query, filters);
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

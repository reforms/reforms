package com.reforms.orm;

import static org.junit.Assert.*;

import org.junit.Test;

import com.reforms.orm.select.report.model.Report;
import com.reforms.orm.select.report.model.ReportIterator;
import com.reforms.orm.select.report.model.ReportRecord;
import com.reforms.orm.select.report.model.ReportRecordHandler;

public class UTestReportDao extends GoodsDbTest {

    public UTestReportDao() {
        super("UTestReportDao");
    }

    private static final String SELECT_GOODS_FULL_QUERY =
            "SELECT id l#ID, name s#NAME, description s#DESCRIPTION, price n#PRICE, articul s#ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods";

    @Test
    public void runTestReportDao_full_loadReport() throws Exception {
        assertReport(SELECT_GOODS_FULL_QUERY);
    }

    private static final String SELECT_GOODS_SHORT_QUERY =
            "SELECT id l#ID, name, description, price n#PRICE, articul, act_time t#ACT_TIME " +
                    "    FROM goods";

    @Test
    public void runTestReportDao_short_loadReport() throws Exception {
        assertReport(SELECT_GOODS_SHORT_QUERY);
    }

    private static final String SELECT_GOODS_VERY_SHORT_QUERY =
            "SELECT id l#, name, description, price n#, articul, act_time t# " +
                    "    FROM goods";

    @Test
    public void runTestReportDao_very_short_loadReport() throws Exception {
        assertReport(SELECT_GOODS_VERY_SHORT_QUERY);
    }

    @Test
    public void runTestReportDao_loadReportIterator() throws Exception {
        ReportDao reportDao = new ReportDao();
        try (ReportIterator reportIterator = reportDao.loadReportIterator(h2ds, SELECT_GOODS_FULL_QUERY)) {
            assertTrue(reportIterator.hasNext());
            assertReportRecord(reportIterator.next(), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
            assertTrue(reportIterator.hasNext());
            assertReportRecord(reportIterator.next(), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
            assertTrue(reportIterator.hasNext());
            assertReportRecord(reportIterator.next(), "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
            assertFalse(reportIterator.hasNext());
        }
    }

    @Test
    public void runTestReportDao_handleReport() throws Exception {
        ReportDao reportDao = new ReportDao();
        reportDao.handleReport(h2ds, SELECT_GOODS_FULL_QUERY, new ReportRecordHandler() {
            int index = 0;

            @Override
            public void startHandle() {
                index = 0;
            }

            @Override
            public boolean handleReportRecord(ReportRecord reportRecord) {
                index++;
                if (index == 1) {
                    assertReportRecord(reportRecord, "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
                } else if (index == 2) {
                    assertReportRecord(reportRecord, "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
                } else if (index == 3) {
                    assertReportRecord(reportRecord, "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
                } else {
                    fail("Не допустимый индекс");
                }
                return true;
            }

            @Override
            public void endHandle() {
                assertEquals(3, index);
            }
        });
    }

    private void assertReport(String query) throws Exception {
        ReportDao reportDao = new ReportDao();
        Report report = reportDao.loadReport(h2ds, query);
        assertReportRecord(report.get(0), "1", "Тапочки", "Мягкие", "100.00", "TR-75", "01.01.2017 19:12:01.690");
        assertReportRecord(report.get(1), "2", "Подушки", "Белые", "200.00", "PR-75", "02.01.2017 19:13:01.690");
        assertReportRecord(report.get(2), "3", "Одеяло", "Пуховое", "300.00", "ZR-75", "03.01.2017 19:14:01.690");
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

package com.reforms.orm;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.reforms.orm.dao.report.model.Report;
import com.reforms.orm.dao.report.model.ReportRecord;

/**
 * TODO доработка - добавить тесты на is null/ is not null
 * @author evgenie
 */
public class UTestReportDaoQuestionFilter extends GoodsDbTest {

    public UTestReportDaoQuestionFilter() {
        super("UTestReportDaoQuestionFilter");
    }

    private static final String SELECT_GOODS_SIMPLE_QUERY =
            "SELECT id l#ID, name NAME, description DESCRIPTION, price n#PRICE, articul ARTICUL, act_time t#ACT_TIME " +
                    "    FROM goods WHERE id = ? AND name = ? AND price = ?";

    @Test
    public void runTest_QuestionFilter() throws Exception {
        assertSimpleReport(SELECT_GOODS_SIMPLE_QUERY, 1L, "Тапочки", new BigDecimal("100.00"));
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

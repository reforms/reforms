package com.reforms.orm.dao;

import com.reforms.orm.select.report.model.ReportRecord;

public interface ReportRecordHandler {

    public void startHandle();

    public boolean handleReportRecord(ReportRecord reportRecord);

    public void endHandle();

}

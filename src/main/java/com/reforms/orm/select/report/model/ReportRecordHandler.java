package com.reforms.orm.select.report.model;

public interface ReportRecordHandler {

    public void startHandle();

    public boolean handleReportRecord(ReportRecord reportRecord);

    public void endHandle();

}

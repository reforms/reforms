package com.reforms.orm.dao.adapter;

import com.reforms.orm.dao.ReportIterator;
import com.reforms.orm.dao.ReportRecordHandler;
import com.reforms.orm.select.report.model.Report;

public interface IReportDaoAdapter extends ISelectedColumnFilterAdapter<IReportDaoAdapter>,
                                           IFilterValuesAdapter<IReportDaoAdapter>,
                                           IPageFilterAdapter<IReportDaoAdapter> {

    Report loadReport() throws Exception;

    ReportIterator iterate() throws Exception;

    void handle(ReportRecordHandler handler) throws Exception;

}

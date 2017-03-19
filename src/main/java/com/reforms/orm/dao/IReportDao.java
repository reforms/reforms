package com.reforms.orm.dao;

import com.reforms.orm.select.report.model.Report;

/**
 * Контракт на совершение базовых операций к БД
 * @author evgenie
 */
interface IReportDao {

    Report loadReport(DaoContext daoCtx) throws Exception;

    ReportIterator iterate(DaoContext daoCtx) throws Exception;

    void handle(DaoContext daoCtx, ReportRecordHandler handler) throws Exception;

}

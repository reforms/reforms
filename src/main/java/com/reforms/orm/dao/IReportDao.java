package com.reforms.orm.dao;

import com.reforms.orm.dao.report.model.Report;

/**
 * Контракт на совершение базовых операций к БД
 * @author evgenie
 */
interface IReportDao {

    Report loadReport(DaoSelectContext daoCtx) throws Exception;

    ReportIterator iterate(DaoSelectContext daoCtx) throws Exception;

    void handle(DaoSelectContext daoCtx, ReportRecordHandler handler) throws Exception;

}

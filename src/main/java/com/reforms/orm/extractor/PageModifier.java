package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.filter.page.IPageFilter;
import com.reforms.orm.dao.filter.page.PageFilter;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.PageStatement;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;
import com.reforms.sql.parser.SqlParser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.filter.column.AllSelectedColumnFilter.ALL_COLUMNS_FILTER;
import static com.reforms.sql.db.DbType.*;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_LIMIT;
import static com.reforms.sql.expr.term.value.PageQuestionExpression.PQE_OFFSET;

/**
 * Prepapre sql-query to be ready for partition loading of data.
 * @author evgenie
 */
@ThreadSafe
public class PageModifier {

    public PageModifier() {
    }

    public IPageFilter changeSelectQuery(SelectQuery selectQuery, IPageFilter pageFilter) {
        DbTypeExtractor dbTypeExtractor = OrmConfigurator.getInstance(DbTypeExtractor.class);
        DbType dbType = dbTypeExtractor.extractDbType(selectQuery);
        if (DBT_POSTGRESQL == dbType || DBT_MIX == dbType) {
            return changeSelectQueryWhenPostgreSql(selectQuery, pageFilter);
        }
        if (DBT_ORACLE == dbType) {
            return changeSelectQueryWhenOracle(selectQuery, pageFilter);
        }
        throw new IllegalStateException("Постраничная разбивка временно не поддерживается для СУБД с типом '" + dbType + "'");
    }

    private IPageFilter changeSelectQueryWhenPostgreSql(SelectQuery selectQuery, IPageFilter pageFilter) {
        PageStatement pageStatement = new PageStatement();
        Object pageLimit = pageFilter.getPageLimit();
        if (pageLimit != null) {
            LimitExpression limitExpr = new LimitExpression();
            limitExpr.setLimitExpr(new PageQuestionExpression(PQE_LIMIT));
            pageStatement.setLimitExpr(limitExpr);
            selectQuery.setPageStatement(pageStatement);
        }
        Object pageOffset = pageFilter.getPageOffset();
        if (pageOffset != null) {
            OffsetExpression offsetExpr = new OffsetExpression();
            offsetExpr.setOffsetExpr(new PageQuestionExpression(PQE_OFFSET));
            pageStatement.setOffsetExpr(offsetExpr);
            selectQuery.setPageStatement(pageStatement);
        }
        return pageFilter;
    }

    private static final String ORACLE_ROWNUM_NAME = "__RN__";

    private static final String ORACLE_SQL_PAGE_TEMPLATE = "SELECT * FROM ({0}, ROWNUM {1} FROM ({2})) WHERE {3}";

    /**
     * ---------------------------------------------|               USER QUERY                                      |--------------------------
     * SELECT * FROM (SELECT c1, c2, ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?
     */
    private IPageFilter changeSelectQueryWhenOracle(SelectQuery selectQuery, IPageFilter pageFilter) {
        SelectStatement selectStatement = selectQuery.getSelectStatement();
        if (selectStatement == null) {
            throw new IllegalStateException("Не поддерживаемый вид sql запроса для постраничной загрузки: '" + selectQuery + "'");
        }
        // {0}
        String selectedColumnQuery = selectStatement.toString();
        // {1}
        String oracleRownumName = makeOracleRownumName(selectStatement, ORACLE_ROWNUM_NAME);
        // {2}
        String originalQuery = selectQuery.toString();
        // {3}
        StringBuilder pageFilterConditions = new StringBuilder();
        Integer pageOffset = pageFilter.getPageOffset();
        if (pageOffset != null) {
            pageFilterConditions.append(oracleRownumName).append(" > ?:O");
        }
        Integer pageLimit = pageFilter.getPageLimit();
        if (pageLimit != null) {
            if (pageOffset != null) {
                pageFilterConditions.append(" AND ");
            }
            pageFilterConditions.append(oracleRownumName).append(" <= ?:L");
        }
        Integer newPageLimit = sumIntegerValue(pageLimit, pageOffset);
        IPageFilter oraclePageFilter = new PageFilter(newPageLimit, pageOffset);

        String resultQueryValue = MessageFormat.format(ORACLE_SQL_PAGE_TEMPLATE, selectedColumnQuery, oracleRownumName, originalQuery, pageFilterConditions);
        SqlParser parser = new SqlParser(resultQueryValue);
        SelectQuery resultQuery = parser.parseSelectQuery();
        SelectQuery.softCopyTo(selectQuery, resultQuery);

        return oraclePageFilter;
    }

    private String makeOracleRownumName(SelectStatement selectStatement, String recommendedName) {
        SelectColumnExtractorAndAliasModifier extractor = getInstance(SelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn>  selectedColumns = extractor.extractSelectedColumns(selectStatement, ALL_COLUMNS_FILTER);
        List<String> usedNames = new ArrayList<>(selectedColumns.size() * 2);
        for (SelectedColumn selectedColumn : selectedColumns) {
            String columnName = selectedColumn.getColumnName();
            if (columnName != null) {
                usedNames.add(columnName.toUpperCase());
            }
            ColumnAlias cAlias = selectedColumn.getColumnAlias();
            if (cAlias != null && cAlias.getSqlAliasKey() != null) {
                usedNames.add(cAlias.getSqlAliasKey().toUpperCase());
            }
        }
        String targetName = recommendedName;
        while (usedNames.contains(targetName)) {
            targetName = "_" + targetName;
        }
        return targetName;
    }

    private Integer sumIntegerValue(Integer firstValue, Integer secondValue) {
        if (firstValue == null) {
            return null;
        }
        int v1 = firstValue;
        int v2 = 0;
        if (secondValue != null) {
            v2 = secondValue;
        }
        return v1 + v2;
    }
}
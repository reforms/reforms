package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.expr.query.LinkingSelectQuery;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.FromStatement;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.term.from.TableReferenceExpression;
import com.reforms.sql.expr.term.from.TableSubQueryExpression;

import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_SUB_QUERY_EXPRESSION;

/**
 *
 * @author evgenie
 */
@ThreadSafe
public class SelectStatementExtractor {

    /**
     * Сделать более правильную логику извлечения выбираемых столбцов. Первый из списка может быть * например, а нужно найти реальный список колонок:
     * Пример 1:
     *      SELECT c1, c2 FROM schemaName.tableName
     *      Ожидаемый результат selectStatement - 'SELECT c1, c2';
     * Пример 2:
     *      SELECT * FROM (SELECT c1, c2, ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?
     *      Ожидаемый результат selectStatement - 'SELECT c1, c2' а не '*';
     * Пример 3:
     *      (SELECT 1) UNION ALL (SELECT 2)
     *      Ожидаемый результат selectStatement - 'SELECT 1';
     * @param selectQuery
     * @return
     */
    public SelectStatement extractFirstSelectStatement(SelectQuery selectQuery) {
        // N1
        if (checkSelectStatement(selectQuery)) {
            return extractFromSelectStatement(selectQuery);
        }
        // N3
        if (checkLinkingQuery(selectQuery)) {
            return extractFromLinkingQuery(selectQuery);
        }
        return null;
    }

    private boolean checkSelectStatement(SelectQuery selectQuery) {
        return selectQuery.getSelectStatement() != null;
    }

    private SelectStatement extractFromSelectStatement(SelectQuery selectQuery) {
        SelectStatement selectStatement = selectQuery.getSelectStatement();
        List<SelectableExpression> selectedExprs = selectStatement.getSelectExps();
        // N2
        if (selectedExprs != null && selectedExprs.size() == 1 && ExpressionType.ET_ASTERISK_EXPRESSION == selectedExprs.get(0).getType()) {
            if (checkFromStatement(selectQuery)) {
                return extractFromFromStatement(selectQuery);
            }
            return null;
        }
        // N1
        return selectStatement;
    }

    private boolean checkLinkingQuery(SelectQuery selectQuery) {
        return selectQuery.getSelectStatement() == null && selectQuery.getLinkingQueries().get(0) != null;
    }

    private SelectStatement extractFromLinkingQuery(SelectQuery selectQuery) {
        LinkingSelectQuery linkingSelectQuery = selectQuery.getLinkingQueries().get(0);
        SelectQuery targetSelectQuery = linkingSelectQuery.getLinkedSelectQuery();
        return extractFirstSelectStatement(targetSelectQuery);
    }

    private boolean checkFromStatement(SelectQuery selectQuery) {
        FromStatement fromStatement = selectQuery.getFromStatement();
        if (fromStatement == null || fromStatement.getTableRefExprs() == null || fromStatement.getTableRefExprs().isEmpty()) {
            return false;
        }
        TableReferenceExpression trExpr = fromStatement.getTableRefExprs().get(0);
        return ET_TABLE_SUB_QUERY_EXPRESSION == trExpr.getType();
    }

    private SelectStatement extractFromFromStatement(SelectQuery selectQuery) {
        FromStatement fromStatement = selectQuery.getFromStatement();
        TableReferenceExpression trExpr = fromStatement.getTableRefExprs().get(0);
        SelectQuery refSelectQuery = ((TableSubQueryExpression) trExpr).getSubQueryExpr();
        return extractFirstSelectStatement(refSelectQuery);
    }
}

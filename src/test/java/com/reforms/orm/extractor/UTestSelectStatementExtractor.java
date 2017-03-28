package com.reforms.orm.extractor;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.statement.SelectStatement;
import com.reforms.sql.parser.SqlParser;

import org.junit.Test;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Логика извлечения списка колонок
 * @author evgenie
 */
public class UTestSelectStatementExtractor {

    @Test
    public void testSimplSelectQueryStatement() {
        assertStatementWith("SELECT c1, c2", "SELECT c1, c2 FROM test");
        assertStatementWith("SELECT c1, c2, ROWNUM RN",
                "SELECT * FROM (SELECT c1, c2 , ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?");
        assertStatementWith("SELECT c1, c2", "(SELECT c1, c2) UNION ALL (SELECT c3, c4)");
    }

    private void assertStatementWith(String etalonView, String query) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        SelectStatementExtractor selectStatementExtractor = getInstance(SelectStatementExtractor.class);
        SelectStatement selectStatement = selectStatementExtractor.extractFirstSelectStatement(selectQuery);
        if (selectStatement == null) {
            assertNull("Для запроса '" + query + "' ожидается '" + etalonView + "', а получен 'NULL'", etalonView);
        } else {
            String statementView = selectStatement.toString();
            assertEquals(etalonView, statementView);
        }
    }
}
package com.reforms.orm.extractor;

import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

import org.junit.Test;

import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static org.junit.Assert.*;

/**
 *
 * @author evgenie
 */
public class UTestSelectColumnExtractorAndAliasModifier {

    @Test
    public void testSelectLogic() {
        assertStatementWith("SELECT c1, c2", "SELECT c1, c2 FROM t");
        assertStatementWith("SELECT c1", "SELECT c1, c2! FROM t");
        assertStatementWith("SELECT c1", "SELECT c1, c2 ! FROM t");
        assertStatementWith("SELECT c1", "SELECT c1, c2 AS ! FROM t");
        assertStatementWith("SELECT c1", "SELECT c1, c2 AS c3:! FROM t WHERE c3 > 0");
        assertStatementWith("SELECT c1, c2, ROWNUM RN",
                "SELECT * FROM (SELECT c1, c2 , ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?");
        assertStatementWith("SELECT c1, c2", "(SELECT c1, c2) UNION ALL (SELECT c3, c4)");
    }

    private void assertStatementWith(String selectQueryOnly, String query) {
        assertFor(selectQueryOnly, query, OrmSelectColumnExtractorAndAliasModifier.class);
        assertFor(selectQueryOnly, query, ReportSelectColumnExtractorAndAliasModifier.class);
    }

    private void assertFor(String selectQueryOnly, String query, Class<? extends SelectColumnExtractorAndAliasModifier> classExtractor) {
        SqlParser sqlParser = new SqlParser(selectQueryOnly);
        SelectQuery expectedSelectQuery = sqlParser.parseSelectQuery();
        sqlParser = new SqlParser(query);
        SelectQuery actualSelectQuery = sqlParser.parseSelectQuery();
        SelectColumnExtractorAndAliasModifier columnListExtractor = getInstance(classExtractor);
        List<SelectedColumn> expectedSelectedColumns = columnListExtractor.extractSelectedColumns(expectedSelectQuery);
        List<SelectedColumn> actualSelectedColumns = columnListExtractor.extractSelectedColumns(actualSelectQuery);
        assertSelectedColumns(expectedSelectedColumns, actualSelectedColumns);
    }

    private void assertSelectedColumns(List<SelectedColumn> expectedSelectedColumns, List<SelectedColumn> actualSelectedColumns) {
        assertEquals(expectedSelectedColumns.size(), actualSelectedColumns.size());
        for (int index = 0; index < expectedSelectedColumns.size(); index++) {
            assertSelectedColumn(expectedSelectedColumns.get(index), actualSelectedColumns.get(index));
        }
    }

    private void assertSelectedColumn(SelectedColumn expectedSelectedColumn, SelectedColumn actualSelectedColumn) {
        if (expectedSelectedColumn == null) {
            assertNull(actualSelectedColumn);
        } else {
            assertNotNull(actualSelectedColumn);
            assertEquals(expectedSelectedColumn.getIndex(), actualSelectedColumn.getIndex());
            assertEquals(expectedSelectedColumn.getPrefixColumnName(), actualSelectedColumn.getPrefixColumnName());
            assertEquals(expectedSelectedColumn.getFieldName(), actualSelectedColumn.getFieldName());
            assertEquals(expectedSelectedColumn.getColumnName(), actualSelectedColumn.getColumnName());
            assertColumnAlias(expectedSelectedColumn.getColumnAlias(), actualSelectedColumn.getColumnAlias());
        }
    }

    private void assertColumnAlias(ColumnAlias expectedColumnAlias, ColumnAlias actualColumnAlias) {
        if (expectedColumnAlias == null) {
            assertNull(actualColumnAlias);
        } else {
            assertNotNull(actualColumnAlias);
            assertEquals(expectedColumnAlias.getAlias(), actualColumnAlias.getAlias());
            assertEquals(expectedColumnAlias.getAliasPrefix(), actualColumnAlias.getAliasPrefix());
            assertEquals(expectedColumnAlias.getAliasType(), actualColumnAlias.getAliasType());
            assertEquals(expectedColumnAlias.getExtra(), actualColumnAlias.getExtra());
            assertEquals(expectedColumnAlias.getJavaAliasKey(), actualColumnAlias.getJavaAliasKey());
            assertEquals(expectedColumnAlias.getSqlAliasKey(), actualColumnAlias.getSqlAliasKey());
        }
    }
}

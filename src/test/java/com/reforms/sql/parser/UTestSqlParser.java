package com.reforms.sql.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;
import com.reforms.sql.parser.SqlParser;

/**
 * 1.
 * SELECT client_id, name_cln, addr_cln
        FROM ibank2.clients
                WHERE (client_id, name_cln, addr_cln) = (60, 'архивный юрик', 'moskow');
    2.
    assertWhereStatement("WHERE (1) MATCH (SELECT client_id FROM ibank2.clients)");
 * @author evgenie
 */
public class UTestSqlParser {

    @Test
    public void testSingleArgSelectStatement() {
        assertSelectQuery("SELECT 2");
        assertSelectQuery("SELECT t.*");
        assertSelectQuery("SELECT '2'");
        assertSelectQuery("SELECT -1.79E+30");
        assertSelectQuery("SELECT -1.79E-30");
        assertSelectQuery("SELECT -1E+3");
        assertSelectQuery("SELECT age");
        assertSelectQuery("SELECT age AS AGE");
        assertSelectQuery("SELECT MAX(age)");
        assertSelectQuery("SELECT MAX(age + 2)");
        assertSelectQuery("SELECT MAX(age + (lower + 2))");
        assertSelectQuery("SELECT MAX((age + (lower + 2)))");
        assertSelectQuery("SELECT MAX('123')");
        assertSelectQuery("SELECT FUNC(age1, age2)");
        assertSelectQuery("SELECT 2 + 3");
        assertSelectQuery("SELECT 2 + 3 AS five");
        assertSelectQuery("SELECT 2 + 3 * 4 + 12 / 15");
        assertSelectQuery("SELECT 2 + 3 * 4 + 12 / 15 AS expr");
        assertSelectQuery("SELECT '2' || '3'");
        assertSelectQuery("SELECT '2' || '3' AS strFive");
        assertSelectQuery("SELECT *");
        assertSelectQuery("SELECT NULL");
        assertSelectQuery("SELECT TRUE");
        assertSelectQuery("SELECT FALSE");
        assertSelectQuery("SELECT ?");
        assertSelectQuery("SELECT ALL age");
        assertSelectQuery("SELECT DISTINCT age");
        assertSelectQuery("SELECT (SELECT name) AS client_name");
        assertSelectQuery("SELECT ((2 + 3) * 4 - 5) * columnName");
        assertSelectQuery("SELECT CASE WHEN client_id = 2 THEN 20 WHEN client_id = 3 THEN 30 ELSE 40 END AS client_name FROM ibank2.clients");
        assertSelectQuery("SELECT (CASE WHEN client_id = 2 THEN 20 WHEN client_id = 3 THEN 30 ELSE 40 END) AS client_name FROM ibank2.clients");
        assertSelectQuery("SELECT CASE client_id WHEN 2 THEN 20 WHEN 3 THEN 30 ELSE 40 END AS client_name FROM ibank2.clients");
        assertSelectQuery("SELECT CASE (client_id + 2) WHEN 2 THEN 20 WHEN 3 THEN 30 ELSE 40 END AS client_name FROM ibank2.clients");
        assertSelectQuery("SELECT CASE (client_id + 2) WHEN (3 + 2) THEN (20 - 4) WHEN (3 * 5) THEN (30 / 10) ELSE (5 - 4) END AS client_name FROM ibank2.clients");
        assertSelectQuery("SELECT CAST('123' AS VARCHAR(5))");
        assertSelectQuery("SELECT CAST('123' AS NUMERIC) FROM ibank2.clients cl");
        assertSelectQuery("SELECT COUNT(DISTINCT soato) FROM ibank2.clients cl");
        assertSelectQuery("SELECT COUNT(ALL soato) FROM ibank2.clients cl");
        assertSelectQuery("SELECT name \"Имя\" FROM local.users");
        assertSelectQuery("SELECT name AS \"Полное Имя\" FROM local.users");

    }

    @Test
    public void testDoubleArgSelectStatement() {
        assertSelectQuery("SELECT 2, 2");
        assertSelectQuery("SELECT '2', '2'");
        assertSelectQuery("SELECT t.*, k.*");
        assertSelectQuery("SELECT -1.79E+30, -1.79E+30");
        assertSelectQuery("SELECT -1.79E-30, -1.79E-30");
        assertSelectQuery("SELECT -1E+3, -1E+3");
        assertSelectQuery("SELECT age, age1");
        assertSelectQuery("SELECT age AS AGE, age1 AS AGE1");
        assertSelectQuery("SELECT MAX(age), MIN(age)");
        assertSelectQuery("SELECT MAX('123'), MAX('789')");
        assertSelectQuery("SELECT FUNC(age1, age2), FUNC2(age1, age2)");
        assertSelectQuery("SELECT FUNC(age1 + 4, age2 - 3), FUNC2(age1 + (a * 4 - 3), age2)");
        assertSelectQuery("SELECT NULLIF(MakeFlag, FinishedGoodsFlag)");
        assertSelectQuery("SELECT 2 + 3, 4 + 5");
        assertSelectQuery("SELECT 2 + 3 AS five, 2 + 3 AS five2");
        assertSelectQuery("SELECT 2 + 3 * 4 + 12 / 15, 2 + 3 * 4 + 12 / 16");
        assertSelectQuery("SELECT 2 + 3 * 4 + 12 / 15 AS expr, 2 + 3 * 4 + 12 / 15 AS expr2");
        assertSelectQuery("SELECT '2' || '3', '4' || '5'");
        assertSelectQuery("SELECT '2' || '3' AS strFive, '4' || '5' AS strNine");
        assertSelectQuery("SELECT *, client_name");
        assertSelectQuery("SELECT NULL, NULL");
        assertSelectQuery("SELECT TRUE, TRUE");
        assertSelectQuery("SELECT FALSE, FALSE");
        assertSelectQuery("SELECT ?, ?");
        assertSelectQuery("SELECT ALL age, age2");
        assertSelectQuery("SELECT DISTINCT age, age2");
        assertSelectQuery("SELECT (SELECT name) AS client_name, (SELECT name) AS client_name2");
        assertSelectQuery("SELECT id AS \"Идентификатор\", name \"Имя\" FROM local.users");
        assertSelectQuery("SELECT id AS \"Идентификатор\", name \"Полное Имя\" FROM local.users");
    }

    @Test
    public void testFromStatement() {
        assertSelectQuery("SELECT 2 FROM tableName");
        assertSelectQuery("SELECT 2 FROM tableName1, tableName2");
        assertSelectQuery("SELECT 2 FROM tableName AS tn");
        assertSelectQuery("SELECT 2 FROM tableName AS tn, tableName1 AS tn1");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName, schemaName.tableName");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName AS stn");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName AS stn, schemaName.tableName AS stn");

        assertSelectQuery("SELECT cl.cl1 FROM (SELECT client_id AS cl1 FROM ibank2.clients) cl");
        assertSelectQuery("SELECT cl1.cl1, cl2.cl2 " +
                "FROM (SELECT client_id AS cl1 FROM ibank2.clients) AS cl1, " +
                "(SELECT client_id AS cl2 FROM ibank2.clients) AS cl2");

        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p LEFT OUTER JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p LEFT JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p INNER JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p RIGHT OUTER JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p RIGHT JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p FULL JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p RIGHT OUTER JOIN ibank2.clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM ibank2.payment p, ibank2.clients cl LEFT OUTER JOIN ibank2.c2accounts c2a ON cl.client_id = c2a.client_id");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) AS v(a, b)");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) v(a, b)");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) v");
        assertSelectQuery("SELECT * FROM ibank2.clients cl, (VALUES (1), (2), (3)) v(a)");
    }

    @Test
    public void testWhereStatement() {
        assertConditionStatement(false);
    }

    private void assertConditionStatement(boolean havingFlag) {
        assertConditionStatement("t1.id = t2.id", havingFlag);
        assertConditionStatement("t1.id <> t2.id", havingFlag);
        assertConditionStatement("t1.id < t2.id", havingFlag);
        assertConditionStatement("t1.id > t2.id", havingFlag);
        assertConditionStatement("t1.id >= t2.id", havingFlag);
        assertConditionStatement("t1.id <= t2.id", havingFlag);
        assertConditionStatement("t1.id <= ?", havingFlag);
        assertConditionStatement("t1.id != ?", havingFlag);
        assertConditionStatement("t1.id = TRUE", havingFlag);
        assertConditionStatement("t1.id = FALSE", havingFlag);
        assertConditionStatement("t1.id + 2 = t2.id + 7", havingFlag);
        assertConditionStatement("t1.id - 2 = t2.id - 7", havingFlag);
        assertConditionStatement("t1.id / 2 = t2.id / 7", havingFlag);
        assertConditionStatement("t1.id * 2 = t2.id * 7", havingFlag);
        assertConditionStatement("t1.id * (t.x + t.y) = t2.id * 7", havingFlag);
        assertConditionStatement("t1.id * (t.x || t.y) = t2.id * 7", havingFlag);
        assertConditionStatement("t1.id = t2.id AND t1.name = t2.name", havingFlag);
        assertConditionStatement("(t1.id = t2.id AND t1.name = t2.name)", havingFlag);
        assertConditionStatement("(t1.id = t2.id AND t1.name = t2.name) OR t1.val <> t2.val", havingFlag);
        assertConditionStatement("t1.id = (SELECT 1)", havingFlag);
        assertConditionStatement("(SELECT 1) = t1.id", havingFlag);
        assertConditionStatement("((SELECT 1) = t1.id OR t2.id = (SELECT 2))", havingFlag);
        // IS NULL/IS NOT NULL
        assertConditionStatement("t1.id IS NOT NULL", havingFlag);
        assertConditionStatement("t1.id IS NULL", havingFlag);
        assertConditionStatement("(SELECT 1) IS NOT NULL", havingFlag);
        assertConditionStatement("(SELECT 1) IS NULL", havingFlag);
        // EXISTS
        assertConditionStatement("NOT EXISTS (SELECT 1 FROM ibank2.clients)", havingFlag);
        assertConditionStatement("EXISTS (SELECT 1 FROM ibank2.clients)", havingFlag);
        // BETWEEN
        assertConditionStatement("t1.value BETWEEN t1.value1 AND t2.value2", havingFlag);
        assertConditionStatement("t1.value NOT BETWEEN t1.value1 AND t2.value2", havingFlag);
        // NOT
        assertConditionStatement("t1.column4 = TRUE OR NOT t2.column4 OR t2.column5", havingFlag);
        assertConditionStatement("t1.column4 = TRUE OR (NOT t2.column4) OR t2.column5", havingFlag);
        assertConditionStatement("NOT ((client_id, name_cln, addr_cln) = (60, 'архивный юрик', 'moskow') OR client_id = 60)", havingFlag);
        // IN
        assertConditionStatement("t1 IN (SELECT id FROM tableName3 t3)", havingFlag);
        assertConditionStatement("t1 IN (?, ?, ?)", havingFlag);
        assertConditionStatement("t1 IN (?, t2.column3, ?)", havingFlag);
        assertConditionStatement("t1 IN (1)", havingFlag);
        assertConditionStatement("t1 IN (1, 2)", havingFlag);
        assertConditionStatement("t1 IN ('1', '2')", havingFlag);
        // LIKE
        assertConditionStatement("UPPER(td.text_field) LIKE ? ESCAPE '#'", havingFlag);
        assertConditionStatement("LOWER(t2.name_cln) LIKE ?", havingFlag);
        assertConditionStatement("LOWER(t2.name_cln) LIKE '12' || '_13' ESCAPE '_'", havingFlag);
        assertConditionStatement("LOWER(t2.name_cln) LIKE ('12' || '_13') ESCAPE '_'", havingFlag);
        assertConditionStatement("UPPER(t2.name_cln) LIKE ('12' || '_13') ESCAPE ('' || '_')", havingFlag);
        // QUANTIFIER
        assertConditionStatement("2 = ANY (SELECT client_id FROM ibank2.clients cl)", havingFlag);
        assertConditionStatement("2 = ALL (SELECT client_id FROM ibank2.clients cl)", havingFlag);
        assertConditionStatement("2 = SOME (SELECT client_id FROM ibank2.clients cl)", havingFlag);
        // UNIQUE
        assertConditionStatement("UNIQUE (SELECT client_id FROM ibank2.clients)", havingFlag);
        assertConditionStatement("(UNIQUE (SELECT client_id FROM ibank2.clients))", havingFlag);
        // assertWhereStatement("WHERE (1) MATCH (SELECT client_id FROM ibank2.clients)");
        // CASE
        assertConditionStatement("name LIKE '12' OR (CASE WHEN id = ? OR id = ? THEN 0 ELSE id END) > 2", havingFlag);
    }

    @Test
    public void testFilterCondition() {
        assertConditionStatement("id = :id AND name = :name", false);
        assertConditionStatement("id = :id? AND name = :name?", false);
        assertConditionStatement("id = ::id AND name = ::name", false);
        assertConditionStatement("id IN (:value1, ::value2, :values)", false);
        assertConditionStatement("LOWER(t2.name_cln) LIKE ::value", false);
        assertConditionStatement("NOT ((:value1, ::value2, :value3) = (60, 'архивный юрик', 'moskow') OR client_id = :id)", false);
        assertConditionStatement("t1.value BETWEEN :value1 AND :value2", false);
    }

    private void assertConditionStatement(String condStatement, boolean havingFlag) {
        if (havingFlag) {
            assertHavingStatement(condStatement);
        } else {
            assertWhereStatement(condStatement);
        }
    }

    private void assertWhereStatement(String whereStatement) {
        assertSelectQuery("SELECT 2 FROM tableName1 t1, tableName2 t2 WHERE " + whereStatement);
    }

    @Test
    public void testGroupByStatement() {
        assertGroupByStatement("t1.clientName");
        assertGroupByStatement("t1.clientName, t2.clientName2");
    }

    private void assertGroupByStatement(String groupByStatement) {
        assertSelectQuery("SELECT 2 FROM tableName1 t1, tableName2 t2 GROUP BY " + groupByStatement);
    }

    @Test
    public void testHavingStatement() {
        assertConditionStatement(true);
    }

    private void assertHavingStatement(String havingStatement) {
        assertSelectQuery("SELECT 2 FROM tableName1 t1, tableName2 t2 HAVING " + havingStatement);
    }

    @Test
    public void testLinkedSelectStatement() {
        assertSelectQuery("SELECT id, name FROM local.users EXCEPT ALL SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users EXCEPT SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users UNION ALL SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users UNION SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users INTERSECT ALL SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users INTERSECT SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users MINUS ALL SELECT id, name FROM local.users WHERE id > 3");
        assertSelectQuery("SELECT id, name FROM local.users MINUS SELECT id, name FROM local.users WHERE id > 3");
    }

    @Test
    public void testOrderByStatement() {
        assertOrderByStatement("1 ASC, 2 DESC");
        assertOrderByStatement("t1.c1 ASC, t2.c1 DESC");
        assertOrderByStatement("t1.c1, t2.c1");
        assertOrderByStatement("t1.c1 COLLATE SYM2 DESC, t2.c1 COLLATE SYM1 ASC");
        assertOrderByStatement("t1.c1 COLLATE SYM2, t2.c1 COLLATE SYM1");
    }

    private void assertOrderByStatement(String orderByStatement) {
        assertSelectQuery("SELECT t1.c1, t1.c2, t2.c1, t2.c2 FROM tableName1 t1, tableName2 t2 ORDER BY " + orderByStatement);
    }

    private void assertSelectQuery(String query) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        assertQuery(query, selectQuery);
    }

    private void assertQuery(String query, SelectQuery selectQuery) {
        // assertEquals(query.replace(" ", ""), queryToString(selectQuery).replace(" ", ""));
        assertEquals(query, queryToString(selectQuery));
    }

    private String queryToString(SelectQuery selectQuery) {
        SqlBuilder builder = new SqlBuilder();
        selectQuery.view(builder);
        return builder.getQuery();
    }
}
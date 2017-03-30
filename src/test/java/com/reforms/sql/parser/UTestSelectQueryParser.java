package com.reforms.sql.parser;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.viewer.SqlBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 1. assertWhereStatement("WHERE (1) MATCH (SELECT client_id FROM schemeName.test_clns)");
 *
 *
 * @author evgenie
 */
public class UTestSelectQueryParser {

    @Test
    public void testNumbersSelectStatement() {
        assertSelectQueryWithAsClause("SELECT 2");
        assertSelectQueryWithAsClause("SELECT -1.79E+30");
        assertSelectQueryWithAsClause("SELECT -1.79E-30");
        assertSelectQueryWithAsClause("SELECT -1E+3");
    }

    @Test
    public void testStringsSelectStatement() {
        assertSelectQueryWithAsClause("SELECT ''");
        assertSelectQueryWithAsClause("SELECT '2'");
        assertSelectQueryWithAsClause("SELECT '2'''");
    }

    @Test
    public void testConstantsSelectStatement() {
        assertSelectQueryWithAsClause("SELECT NULL");
        assertSelectQueryWithAsClause("SELECT TRUE");
        assertSelectQueryWithAsClause("SELECT FALSE");
    }

    @Test
    public void testAsterisksSelectStatement() {
        assertSelectQueryWithAsClause("SELECT t.*");
        assertSelectQueryWithAsClause("SELECT *");
    }

    @Test
    public void testQuestionsSelectStatement() {
        assertSelectQueryWithAsClause("SELECT ?");
    }

    @Test
    public void testColumnsSelectStatement() {
        assertSelectQueryWithAsClause("SELECT age");
        assertSelectQueryWithAsClause("SELECT \"age\"");
        assertSelectQueryWithAsClause("SELECT \"schemaName\".\"age\"");
    }

    @Test
    public void testFuncsSelectStatement() {
        assertSelectQueryWithAsClause("SELECT MIN(age)");
        assertSelectQueryWithAsClause("SELECT SUM(age)");
        assertSelectQueryWithAsClause("SELECT MAX(age)");
        assertSelectQueryWithAsClause("SELECT AVG(age)");
        assertSelectQueryWithAsClause("SELECT COALESCE(age, 0)");
        assertSelectQueryWithAsClause("SELECT CAST(AVG(reportsto) AS FLOAT)");
        assertSelectQueryWithAsClause("SELECT MAX(age + 2)");
        assertSelectQueryWithAsClause("SELECT MAX(age + (lower + 2))");
        assertSelectQueryWithAsClause("SELECT MAX((age + (lower + 2)))");
        assertSelectQueryWithAsClause("SELECT MAX('123')");
        assertSelectQueryWithAsClause("SELECT FUNC(age1, age2)");
        assertSelectQueryWithAsClause("SELECT CAST('123' AS VARCHAR(5))");
        assertSelectQueryWithAsClause("SELECT CAST('123' AS NUMERIC)");
        assertSelectQueryWithAsClause("SELECT COUNT(DISTINCT soato)");
        assertSelectQueryWithAsClause("SELECT COUNT(ALL soato)");
        assertSelectQueryWithAsClause("SELECT NULLIF(client_id, 2)");
        assertSelectQueryWithAsClause("SELECT UPPER('tot')");
        assertSelectQueryWithAsClause("SELECT LOWER('tot')");
        assertSelectQueryWithAsClause("SELECT NOW()");
    }

    @Test
    public void testCasesSelectStatement() {
        assertSelectQueryWithAsClause("SELECT CASE WHEN client_id = 2 THEN 20 WHEN client_id = 3 THEN 30 ELSE 40 END");
        assertSelectQueryWithAsClause("SELECT (CASE WHEN client_id = 2 THEN 20 WHEN client_id = 3 THEN 30 ELSE 40 END)");
        assertSelectQueryWithAsClause("SELECT CASE client_id WHEN 2 THEN 20 WHEN 3 THEN 30 ELSE 40 END");
        assertSelectQueryWithAsClause("SELECT CASE (client_id + 2) WHEN 2 THEN 20 WHEN 3 THEN 30 ELSE 40 END");
        assertSelectQueryWithAsClause("SELECT CASE (client_id + 2) WHEN (3 + 2) THEN (20 - 4) WHEN (3 * 5) THEN (30 / 10) ELSE (5 - 4) END");
    }

    @Test
    public void testSelectModeSelectStatement() {
        assertSelectQueryWithAsClause("SELECT ALL age");
        assertSelectQueryWithAsClause("SELECT DISTINCT age");
    }

    @Test
    public void testSubSelectSelectStatement() {
        assertSelectQueryWithAsClause("SELECT (SELECT name)");
    }

    @Test
    public void testMathSelectStatement() {
        assertSelectQueryWithAsClause("SELECT 2 + 3");
        assertSelectQueryWithAsClause("SELECT 2 + 3");
        assertSelectQueryWithAsClause("SELECT 2 + 3 * 4 + 12 / 15");
        assertSelectQueryWithAsClause("SELECT 2 + 3 * 4 + 12 / 15");
        assertSelectQueryWithAsClause("SELECT '2' || '3'");
        assertSelectQueryWithAsClause("SELECT '2' || '3'");
        assertSelectQueryWithAsClause("SELECT ((2 + 3) * 4 - 5) * columnName");
    }

    @Test
    public void testCommonSqlDateAndTime() {
        assertSelectQueryWithAsClause("SELECT NOW() AT TIME ZONE 'UTC'");
        assertSelectQueryWithAsClause("SELECT '2004-10-19 10:23:54+02' AT TIME ZONE 'UTC'");
        assertSelectQueryWithAsClause("SELECT NOW() AT TIME ZONE 'UTC'");
        assertSelectQueryWithAsClause("SELECT columnId AT TIME ZONE 'UTC'");
        assertSelectQueryWithAsClause("SELECT '2004-10-19 10:23:54+02' AT TIME ZONE 'UTC'");
        assertSelectQueryWithAsClause("SELECT TIME '2004-10-19 10:23:54'");
        assertSelectQueryWithAsClause("SELECT DATE '2004-10-19 10:23:54'");
        assertSelectQueryWithAsClause("SELECT TIMESTAMP '2004-10-19 10:23:54'");
        assertSelectQueryWithAsClause("SELECT INTERVAL '1 day'");
        assertSelectQueryWithAsClause("SELECT {TS '2017-01-01 19:12:01.69'}");
        assertSelectQueryWithAsClause("SELECT {D '2017-01-01'}");
        assertSelectQueryWithAsClause("SELECT {T '19:12:01'}");
    }

    @Test
    public void testMsSqlTopSelectExt() {
        assertSelectQueryWithAsClause("SELECT TOP 50 *");
        assertSelectQueryWithAsClause("SELECT TOP 50 PERCENT *");
        assertSelectQueryWithAsClause("SELECT TOP 50 PERCENT client_id");
        assertSelectQueryWithAsClause("SELECT TOP 50 WITH TIES *");
        assertSelectQueryWithAsClause("SELECT TOP 50 WITH TIES client_id");
        assertSelectQueryWithAsClause("SELECT TOP(50) *");
        assertSelectQueryWithAsClause("SELECT TOP(50) PERCENT *");
        assertSelectQueryWithAsClause("SELECT TOP(50) PERCENT client_id");
        assertSelectQueryWithAsClause("SELECT TOP(50) WITH TIES *");
        assertSelectQueryWithAsClause("SELECT TOP(50) WITH TIES client_id");
        assertSelectQueryWithAsClause("SELECT ALL TOP 50 *");
        assertSelectQueryWithAsClause("SELECT ALL TOP 50 PERCENT *");
        assertSelectQueryWithAsClause("SELECT ALL TOP 50 PERCENT client_id");
        assertSelectQueryWithAsClause("SELECT ALL TOP 50 WITH TIES *");
        assertSelectQueryWithAsClause("SELECT ALL TOP 50 WITH TIES client_id");
        assertSelectQueryWithAsClause("SELECT ALL TOP(50) *");
        assertSelectQueryWithAsClause("SELECT ALL TOP(50) PERCENT *");
        assertSelectQueryWithAsClause("SELECT ALL TOP(50) PERCENT client_id");
        assertSelectQueryWithAsClause("SELECT ALL TOP(50) WITH TIES *");
        assertSelectQueryWithAsClause("SELECT ALL TOP(50) WITH TIES client_id");
    }

    @Test
    public void testMySqlDateAndTime() {
        assertSelectQueryWithAsClause("SELECT CONVERT(datetime2(0), '2015-03-29T01:01:00', 126) AT TIME ZONE 'Central European Standard Time'");
        assertSelectQueryWithAsClause("SELECT SalesOrderID, OrderDate, OrderDate AT TIME ZONE 'Pacific Standard Time'");
    }

    @Test
    public void testPostgreSqlDateAndTime() {
        // Общие
        assertSelectQueryWithAsClause("SELECT TIME '2004-10-19 10:23:54'");
        //assertSelectQuery("SELECT TIME WITH TIME ZONE '2004-10-19 10:23:54+02'");
        assertSelectQueryWithAsClause("SELECT TIMESTAMP '2004-10-19 10:23:54'");
        //assertSelectQuery("SELECT TIMESTAMP WITH TIME ZONE '2004-10-19 10:23:54+02'");
        //  Временная арифметика
        assertSelectQuery("SELECT '2012-01-05'::DATE - '2012-01-01'::DATE AS result");
        assertSelectQuery("SELECT '2012-01-05'::TIMESTAMP - '2012-01-01'::TIMESTAMP AS result");
        assertSelectQuery("SELECT '2012-01-05'::TIMESTAMP - '1 hour'::INTERVAL AS result");
        assertSelectQuery("SELECT '2010-05-06'::DATE + INTERVAL '1 month 1 day 1 minute' AS result");
        assertSelectQuery("SELECT '1 hour'::interval / 7 AS result");
        assertSelectQueryWithAsClause("SELECT INTERVAL '1 minute' * 99");
        assertSelectQueryWithAsClause("SELECT INTERVAL '1 hour' - interval '33 minutes'");
        assertSelectQueryWithAsClause("SELECT INTERVAL '1 hour 27 minutes' + interval '33 minutes'");
        // Специальные значения времени
        assertSelectQuery("SELECT 'epoch'::TIMESTAMP");
        assertSelectQuery("SELECT 'infinity'::TIMESTAMP");
        assertSelectQuery("SELECT '-infinity'::TIMESTAMP");
        assertSelectQuery("SELECT 'today'::TIMESTAMP, NOW()::DATE");
        assertSelectQuery("SELECT 'now'::TIMESTAMP, NOW()");
        assertSelectQuery("SELECT 'tomorrow'::TIMESTAMP, now()::DATE");
        assertSelectQuery("SELECT 'yesterday'::TIMESTAMP, now()::DATE");
        assertSelectQuery("SELECT 'allballs'::TIME");
        // Полезные функции
        assertSelectQuery("SELECT (TIMESTAMP '2010-06-12 20:11')::DATE");
        assertSelectQueryWithAsClause("SELECT DATE_TRUNC('month', timestamp '2010-06-12 20:11')");
        assertSelectQueryWithAsClause("SELECT DATE_TRUNC('week', timestamp '2010-06-12 20:11')");
        assertSelectQueryWithAsClause("SELECT DATE_TRUNC('quarter', timestamp '2010-06-12 20:11')");
        assertSelectQueryWithAsClause("SELECT DATE_TRUNC('year', timestamp '2010-06-12 20:11')");
        // Получение полей времени (года, месяца, недели, дня, часа, минуты, секунды и т. д.)
        //assertSelectQuery("SELECT EXTRACT(year FROM now()), date_part('year', now()), now()::date");
        //assertSelectQueryWithAsClause("SELECT EXTRACT(month FROM NOW()), date_part('month', NOW()), NOW()");
        //assertSelectQuery("SELECT EXTRACT(dow FROM NOW()), date_part('dow', NOW()), NOW()::date");
    }

    private static final String[] AS_CLAUSE_FULL_VARIANTS = new String[] {
            "::DATE",
            "::TIME",
            "::VARCHAR(5)",
            " \"Первая\"\" колонка\"",
            " firstColumn",
            " AS firstColumn",
            " \"Первая колонка\"",
            " AS \"Первая колонка\"",
    };

    private void assertSelectQueryWithAsClause(String query) {
        for (String asClause : AS_CLAUSE_FULL_VARIANTS) {
            assertSelectQuery(query + asClause);
        }
    }

    @Test
    public void testSingleArgSelectMapStatement() {
        // Результат без алиаса
        assertSelectQuery("SELECT age AS bobj1.bobj2.bobj3");
        // Результат с алиаса b3
        assertSelectQuery("SELECT age AS b3:bobj1.bobj2.bobj3");
        // Результат с алиаса b3, в качестве сетера будет setAge
        assertSelectQuery("SELECT age AS b3:");
        // Результат без алиаса: getBobj1().getBobj2().setBobj3
        assertSelectQuery("SELECT age AS t#bobj1.bobj2.bobj3");
        // Результат с алиаса b3
        assertSelectQuery("SELECT age AS b3:t#bobj1.bobj2.bobj3");
        // Результат с алиаса b3, в качестве сетера будет setAge и типом timestamp
        assertSelectQuery("SELECT age AS b3:t#");
        // Результат в качестве сетера будет setAge и типом timestamp
        assertSelectQuery("SELECT age AS t#");
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
        assertSelectQuery("SELECT 2 FROM schemaName.[tableName]");
        assertSelectQuery("SELECT 2 FROM schemaName.\"tableName\"");
        assertSelectQuery("SELECT 2 FROM \"schemaName\".[tableName]");
        assertSelectQuery("SELECT 2 FROM \"schemaName\".\"tableName\"");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName, schemaName.tableName");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName AS stn");
        assertSelectQuery("SELECT 2 FROM schemaName.tableName AS stn, schemaName.tableName AS stn");

        assertSelectQuery("SELECT cl.cl1 FROM (SELECT client_id AS cl1 FROM schemeName.test_clients) cl");
        assertSelectQuery("SELECT cl1.cl1, cl2.cl2 " +
                "FROM (SELECT client_id AS cl1 FROM schemeName.test_clients) AS cl1, " +
                "(SELECT client_id AS cl2 FROM schemeName.test_clients) AS cl2");

        assertSelectQuery("SELECT * FROM schemeName.test_clients cln CROSS JOIN schemeName.accounts acc WHERE cln.client_id = acc.id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p LEFT OUTER JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p LEFT JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p INNER JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p RIGHT OUTER JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p RIGHT JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p FULL OUTER JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p FULL JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p RIGHT OUTER JOIN schemeName.test_clients cl ON p.client_id = cl.client_id");
        assertSelectQuery("SELECT doc_id, cl.name_cln FROM schemeName.payment p, schemeName.test_clients cl LEFT OUTER JOIN schemeName.c2accounts c2a ON cl.client_id = c2a.client_id");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) AS v(a, b)");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) v(a, b)");
        assertSelectQuery("SELECT * FROM (VALUES (1, 2), (2, 3)) v");
        assertSelectQuery("SELECT * FROM schemeName.test_clients cl, (VALUES (1), (2), (3)) v(a)");
        assertSelectQuery("SELECT * FROM schemeName.test_clients INNER JOIN (VALUES (1), (2), (1)) AS v(k) ON k = 1");
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
        assertConditionStatement("NOT EXISTS (SELECT 1 FROM schemeName.test_clients)", havingFlag);
        assertConditionStatement("EXISTS (SELECT 1 FROM schemeName.test_clients)", havingFlag);
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
        assertConditionStatement("2 = ANY (SELECT client_id FROM schemeName.test_clients cl)", havingFlag);
        assertConditionStatement("2 = ALL (SELECT client_id FROM schemeName.test_clients cl)", havingFlag);
        assertConditionStatement("2 = SOME (SELECT client_id FROM schemeName.test_clients cl)", havingFlag);
        // UNIQUE
        assertConditionStatement("UNIQUE (SELECT client_id FROM schemeName.test_clients)", havingFlag);
        assertConditionStatement("(UNIQUE (SELECT client_id FROM schemeName.test_clients))", havingFlag);
        // assertWhereStatement("WHERE (1) MATCH (SELECT client_id FROM schemeName.test_clients)");
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
        assertSelectQuery("(SELECT client_id, 1 as filter FROM schemeName.test_clients " +
                "WHERE group_id = 1 AND act_time >= NOW() " +
                "ORDER BY client_id DESC) " +
                "UNION ALL " +
                "(SELECT client_id, 2 as filter FROM schemeName.test_clients " +
                "WHERE group_id = 1 AND act_time < NOW() " +
                "ORDER BY client_id DESC) " +
                "ORDER BY filter");
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

    @Test
    public void testBigSelectStatement() {
        assertSelectQuery("SELECT cg.id, cg.tcl_group FROM schemeName.tcl_groups cg " +
                "INNER JOIN schemeName.doc_rcpts_groups drg ON drg.doc_id = ? AND drg.group_id = cg.id WHERE cg.tcl_type = 0 AND cg.id " +
                "IN (SELECT cl2cg.tcl_group_id FROM schemeName.clns2client_groups cl2cg WHERE EXISTS (SELECT 1 FROM schemeName.clns cl " +
                "WHERE client_id IN (SELECT client_id FROM schemeName.body2clns WHERE operator_id = ?) AND cl.status <> 0 AND " +
                "(EXISTS (SELECT 1 FROM schemeName.c2accounts c2a, schemeName.accounts a WHERE c2a.tcl_id = cl.tcl_id AND a.id = c2a.account_id " +
                "AND a.branch_id = 1 AND a.status <> 0) OR cl.branch_id = ?) AND cl.tcl_id = cl2cg.tcl_id)) ORDER BY cg.tcl_group");
        assertSelectQuery("SELECT cl.client_id, cl.last_name, cl.first_name, cl.middle_name "
                + "FROM test_scheme.pcl cl, test_scheme.o2cl o2c, test_scheme.cdcrc dr "
                + "WHERE dr.doc_id = ? AND dr.recipient_id = cl.client_id AND cl.status <> 0 AND o2c.client_id = cl.client_id AND o2c.operator_id = ? "
                + "AND cl.client_id IN (SELECT c2a.client_id FROM test_scheme.ccaa c2a, test_scheme.aaa a "
                + "WHERE c2a.account_id = a.id AND a.branch_id = ? AND a.account LIKE ?) "
                + "AND UPPER((CASE WHEN last_name IS NOT NULL THEN last_name ELSE '' END) || ' ' || (CASE WHEN first_name IS NOT NULL THEN first_name ELSE '' END) || ' ' || (CASE WHEN middle_name IS NOT NULL THEN middle_name ELSE '' END)"
                + ") LIKE ?");
    }

    @Test
    public void testPagingQueryForPostgreSql() {
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT 10");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT ?");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT :limitCount");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT ::limitCount");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT 10 OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT 10 OFFSET ?");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT 10 OFFSET ::offsetCount");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT ALL OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 LIMIT ALL");
        assertSelectQuery("SELECT t1 FROM tableName1 OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 ORDER BY t1 LIMIT 10");
        assertSelectQuery("SELECT t1 FROM tableName1 ORDER BY t1 LIMIT 10 OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 ORDER BY t1 LIMIT ALL OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 ORDER BY t1 LIMIT ALL");
        assertSelectQuery("SELECT t1 FROM tableName1 ORDER BY t1 OFFSET 20");
        assertSelectQuery("SELECT t1 FROM tableName1 OFFSET 20 LIMIT 10", "SELECT t1 FROM tableName1 LIMIT 10 OFFSET 20");
    }

    @Test
    public void testPagingQueryForOracle() {
        assertSelectQuery("SELECT * FROM (SELECT c1, c2, ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c1 > 0 ORDER BY 1)) WHERE RN > ? AND RN <= ?");
        assertSelectQuery("SELECT c1, c2 FROM (SELECT c1, c2, ROWNUM RN FROM (SELECT c1, c2 FROM schemaName.tableName WHERE c2 != TRUE ORDER BY 2)) WHERE RN > ? AND RN <= ?");
    }

    @Test
    public void testPagingQueryForMsSql2012() {
        String query =
                "SELECT ROW_NUMBER() OVER(PARTITION BY PostalCode ORDER BY SalesYTD DESC) AS \"Row Number\", p.LastName, s.SalesYTD, a.PostalCode " +
                "FROM Sales.SalesPerson AS s " +
                    "INNER JOIN Person.Person AS p " +
                        "ON s.BusinessEntityID = p.BusinessEntityID " +
                    "INNER JOIN Person.Address AS a " +
                        "ON a.AddressID = p.BusinessEntityID " +
                "WHERE TerritoryID IS NOT NULL " +
                     "AND SalesYTD <> 0 " +
                "ORDER BY PostalCode";
        assertSelectQuery(query);

        query =
            "SELECT SalesOrderID, ProductID, OrderQty, " +
                "SUM(OrderQty) OVER(PARTITION BY SalesOrderID) AS Total, " +
                "AVG(OrderQty) OVER(PARTITION BY SalesOrderID) AS \"Avg\", " +
                "COUNT(OrderQty) OVER(PARTITION BY SalesOrderID) AS \"Count\", " +
                "MIN(OrderQty) OVER(PARTITION BY SalesOrderID) AS \"Min\", " +
                "MAX(OrderQty) OVER(PARTITION BY SalesOrderID) AS \"Max\" " +
            "FROM Sales.SalesOrderDetail " +
            "WHERE SalesOrderID IN (43659, 43664)";
        assertSelectQuery(query);

        query =
            "SELECT BusinessEntityID, TerritoryID, " +
                    "DATEPART(yy, ModifiedDate) AS SalesYear, " +
                    "CONVERT(varchar(20), SalesYTD, 1) AS SalesYTD, " +
                    "CONVERT(varchar(20), AVG(SalesYTD) OVER(PARTITION BY TerritoryID " +
                                                            "ORDER BY DATEPART(yy, ModifiedDate)" +
                                                            "), 1) AS MovingAvg, " +
                    "CONVERT(varchar(20), SUM(SalesYTD) OVER(PARTITION BY TerritoryID " +
                                                            "ORDER BY DATEPART(yy, ModifiedDate)" +
                                                            "), 1) AS CumulativeTotal " +
             "FROM Sales.SalesPerson " +
             "WHERE TerritoryID IS NULL OR TerritoryID < 5 " +
             "ORDER BY TerritoryID, SalesYear";
        assertSelectQuery(query);

        query = "SELECT ROW_NUMBER() OVER(ORDER BY SalesYTD DESC) AS Row, " +
                       "FirstName, LastName, ROUND(SalesYTD, 2, 1) AS \"Sales YTD\" " +
                "FROM Sales.vSalesPerson " +
                "WHERE TerritoryName IS NOT NULL AND SalesYTD <> 0";
        assertSelectQuery(query);

        query = "SELECT name, salary " +
                "FROM (SELECT ROW_NUMBER() OVER(ORDER BY salary) AS rn, " +
                              "emp.* " +
                      "FROM emp) " +
                "WHERE rn BETWEEN 3 AND 5";
        assertSelectQuery(query);

        query = "SELECT * " +
                "FROM (SELECT ROW_NUMBER() OVER(ORDER BY OrderDate) AS RowNum, * " +
                       "FROM Orders " +
                       "WHERE OrderDate >= '1980-01-01'" +
                      ") AS RowConstrainedResult " +
                "WHERE RowNum >= 1 AND RowNum < 20 " +
                "ORDER BY RowNum";
        assertSelectQuery(query);

        //query = "SELECT * FROM TableName ORDER BY id OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY";
        //assertSelectQuery(query);

        /**
         *
            OFFSET excludes the first set of records.
            OFFSET can only be used with an ORDER BY clause.
            OFFSET with FETCH NEXT returns a defined window of records.
            OFFSET with FETCH NEXT is great for building pagination support.
         */

    }

    private void assertSelectQuery(String query) {
        assertSelectQuery(query, null);
    }

    private void assertSelectQuery(String query, String expectedQuery) {
        SqlParser sqlParser = new SqlParser(query);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        assertQuery(expectedQuery != null ? expectedQuery : query, selectQuery);
    }

    private void assertQuery(String query, SelectQuery selectQuery) {
        assertEquals(query, queryToString(selectQuery));
    }

    private String queryToString(SelectQuery selectQuery) {
        SqlBuilder builder = new SqlBuilder();
        selectQuery.view(builder);
        return builder.getQuery();
    }
}
package com.reforms.sql.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evgenie
 */
public class SqlWords {

    public static String SW_SELECT = "SELECT";

    public static String SW_FROM = "FROM";

    public static String SW_WHERE = "WHERE";

    public static String SW_GROUP = "GROUP";

    public static String SW_BY = "BY";

    public static String SW_GROUP_BY = "GROUP BY";

    public static String SW_HAVING = "HAVING";

    public static String SW_ORDER = "ORDER";

    public static String SW_ORDER_BY = "ORDER BY";

    public static String SW_ALL = "ALL";

    public static String SW_DISTINCT = "DISTINCT";

    public static String SW_TOP = "TOP"; // MSSQL

    public static String SW_PERCENT = "PERCENT"; // MSSQL

    public static String SW_WITH_TIES = "WITH TIES"; // MSSQL

    public static String SW_WITH = "WITH"; // part of MSSQL

    public static String SW_TIES = "TIES"; // part of MSSQL

    public static String SW_AS = "AS";

    public static String SW_ON = "ON";

    public static String SW_INNER_JOIN = "INNER JOIN";

    public static String SW_LEFT_OUTER_JOIN = "LEFT OUTER JOIN";

    public static String SW_RIGHT_OUTER_JOIN = "RIGHT OUTER JOIN";

    public static String SW_FULL_OUTER_JOIN = "FULL OUTER JOIN";

    public static String SW_CROSS_JOIN = "CROSS JOIN";

    public static String SW_INNER = "INNER";

    public static String SW_LEFT = "LEFT";

    public static String SW_RIGHT = "RIGHT";

    public static String SW_FULL = "FULL";

    public static String SW_OUTER = "OUTER";

    public static String SW_CROSS = "CROSS";

    public static String SW_JOIN = "JOIN";

    public static String SW_NULL = "NULL";

    public static String SW_OR = "OR";

    public static String SW_AND = "AND";

    public static String SW_TRUE = "TRUE";

    public static String SW_FALSE = "FALSE";

    public static String SW_NOT = "NOT";

    public static String SW_IS = "IS";

    public static String SW_EXISTS = "EXISTS";

    public static String SW_BETWEEN = "BETWEEN";

    public static String SW_CASE = "CASE";

    public static String SW_WHEN = "WHEN";

    public static String SW_THEN = "THEN";

    public static String SW_ELSE = "ELSE";

    public static String SW_END = "END";

    public static String SW_IN = "IN";

    public static String SW_LIKE = "LIKE";

    public static String SW_ESCAPE = "ESCAPE";

    public static String SW_ANY = "ANY";

    public static String SW_SOME = "SOME";

    public static String SW_UNIQUE = "UNIQUE";

    public static String SW_CAST = "CAST";

    public static String SW_VALUES = "VALUES";

    public static String SW_UNION = "UNION";

    public static String SW_EXCEPT = "EXCEPT";

    public static String SW_INTERSECT = "INTERSECT";

    public static String SW_TIME = "TIME";

    public static String SW_DATE = "DATE";

    public static String SW_TIMESTAMP = "TIMESTAMP";

    public static String SW_INTERVAL = "INTERVAL";

    public static String SW_AT_TIME_ZONE = "AT TIME ZONE";

    public static String SW_AT = "AT";

    public static String SW_ZONE = "ZONE";

    public static String SW_MINUS = "MINUS"; // ORACLE

    public static String SW_CORRESPONDING = "CORRESPONDING";

    public static String SW_ASC = "ASC";

    public static String SW_DESC = "DESC";

    public static String SW_COLLATE = "COLLATE";

    public static String SW_LIMIT = "LIMIT"; // POSTGRESQL, MSSQL

    public static String SW_OFFSET = "OFFSET"; // POSTGRESQL, MSSQL

    public static String SW_UPDATE = "UPDATE";

    public static String SW_SET = "SET";

    public static String SW_DELETE = "DELETE";

    public static String SW_DELETE_FROM = "DELETE FROM";

    public static String SW_INSERT = "INSERT";

    public static String SW_INTO = "INTO";

    public static String SW_INSERT_INTO = "INSERT INTO";

    public static String SW_USING = "USING"; // MSSQL

    public static String SW_ROW_NUMBER = "ROW_NUMBER"; // MSSQL

    public static String SW_OVER = "OVER"; // MSSQL

    public static String SW_PARTITION = "PARTITION"; // MSSQL

    public static String SW_FETCH = "FETCH"; // MSSQL

    public static String SW_NEXT = "NEXT"; // MSSQL

    public static String SW_FIRST = "FIRST"; // MSSQL

    public static String SW_ROWS = "ROWS"; // MSSQL

    public static String SW_ROW = "ROW"; // MSSQL

    public static String SW_ONLY = "ONLY"; // MSSQL

    public static String SW_CALL = "CALL";

    public static String SW_RETURNING = "RETURNING"; // PostgreSql

    private static final Map<String, Boolean> WORDS = init();

    private static Map<String, Boolean> init() {
        Map<String, Boolean> words = new HashMap<>();
        addWords(words, SW_SELECT);
        addWords(words, SW_FROM);
        addWords(words, SW_WHERE);
        addWords(words, SW_GROUP_BY);
        addWords(words, SW_HAVING);
        addWords(words, SW_ORDER_BY);
        addWords(words, SW_ALL);
        addWords(words, SW_DISTINCT);
        addWords(words, SW_TOP, SW_WITH, SW_TIES);
        addWords(words, SW_AS);
        addWords(words, SW_ON);
        addWords(words, SW_INNER_JOIN);
        addWords(words, SW_LEFT_OUTER_JOIN);
        addWords(words, SW_RIGHT_OUTER_JOIN);
        addWords(words, SW_FULL_OUTER_JOIN);
        addWords(words, SW_CROSS_JOIN);
        addWords(words, SW_NULL);
        addWords(words, SW_TRUE);
        addWords(words, SW_FALSE);
        addWords(words, SW_NOT);
        addWords(words, SW_IS);
        addWords(words, SW_EXISTS);
        addWords(words, SW_BETWEEN);
        addWords(words, SW_CASE, SW_WHEN, SW_THEN, SW_ELSE, SW_END);
        addWords(words, SW_IN);
        addWords(words, SW_ANY, SW_SOME);
        addWords(words, SW_UNIQUE);
        addWords(words, SW_CAST);
        addWords(words, SW_VALUES);
        addWords(words, SW_UNION, SW_EXCEPT, SW_INTERSECT, SW_MINUS);
        addWords(words, SW_CORRESPONDING);
        addWords(words, SW_ASC, SW_DESC);
        addWords(words, SW_COLLATE);
        addWords(words, SW_TIME, SW_DATE, SW_TIMESTAMP, SW_INTERVAL, SW_AT_TIME_ZONE, SW_AT, SW_ZONE);
        addWords(words, SW_LIMIT, SW_OFFSET);
        addWords(words, SW_UPDATE, SW_SET);
        addWords(words, SW_DELETE);
        addWords(words, SW_INSERT, SW_INTO);
        addWords(words, SW_USING);
        addWords(words, SW_OVER);
        addWords(words, SW_PARTITION);
        addWords(words, SW_FETCH, SW_NEXT, SW_FIRST, SW_ONLY, SW_ROWS, SW_ROW);
        addWords(words, SW_CALL);
        addWords(words, SW_RETURNING);
        return Collections.unmodifiableMap(words);
    }

    private static void addWords(Map<String, Boolean> words, String ... sqlWords) {
        for (String sqlWord : sqlWords) {
            for (String singleWord : sqlWord.split("\\s+")) {
                words.put(singleWord, Boolean.TRUE);
            }
        }
    }

    public static boolean startFrom(String part, String... sqlWords) {
        for (String sqlWord : sqlWords) {
            String firstWord = sqlWord.split("\\s+")[0];
            if (firstWord.equals(part)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSqlWord(String word) {
        return word != null && WORDS.containsKey(word.toUpperCase());
    }
}

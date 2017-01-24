package com.reforms.sql.expr.term.from;

import static com.reforms.sql.expr.term.SqlWords.*;

/**
 *
 * @author evgenie
 */
public enum TableJoinTypes {
    TJT_INNER_JOIN,
    TJT_LEFT_OUTER_JOIN,
    TJT_RIGHT_OUTER_JOIN,
    TJT_FULL_OUTER_JOIN,
    TJT_CROSS_JOIN;

    public static TableJoinTypes resolveJoinType(String swWord) {
        if (swWord == null) {
            return null;
        }
        String preparedSwWord = swWord.toUpperCase().replaceAll(" +", " ").trim();
        if (SW_INNER_JOIN.equals(preparedSwWord)) {
            return TJT_INNER_JOIN;
        }
        if (SW_CROSS_JOIN.equals(preparedSwWord)) {
            return TJT_CROSS_JOIN;
        }
        if (SW_LEFT_OUTER_JOIN.equals(preparedSwWord) || "LEFT JOIN".equals(preparedSwWord)) {
            return TJT_LEFT_OUTER_JOIN;
        }
        if (SW_RIGHT_OUTER_JOIN.equals(preparedSwWord) || "RIGHT JOIN".equals(preparedSwWord)) {
            return TJT_RIGHT_OUTER_JOIN;
        }
        if (SW_FULL_OUTER_JOIN.equals(preparedSwWord) || "FULL JOIN".equals(preparedSwWord)) {
            return TJT_FULL_OUTER_JOIN;
        }
        return null;
    }
}
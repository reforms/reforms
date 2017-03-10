package com.reforms.sql.parser;

import static com.reforms.sql.expr.term.SqlWords.*;

/**
 *
 * @author evgenie
 */
public class OptWords {

    public static final OptWord OW_O_NOT = new OptWord(false, SW_NOT);

    public static final OptWord OW_R_IN = new OptWord(true, SW_IN);

    public static final OptWord OW_R_LIKE = new OptWord(true, SW_LIKE);

    public static final OptWord OW_R_BETWEEN = new OptWord(true, SW_BETWEEN);

    public static final OptWord OW_R_GROUP = new OptWord(true, SW_GROUP);

    public static final OptWord OW_R_BY = new OptWord(true, SW_BY);

    public static final OptWord OW_R_ORDER = new OptWord(true, SW_ORDER);
}

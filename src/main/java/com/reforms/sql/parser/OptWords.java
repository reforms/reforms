package com.reforms.sql.parser;

import static com.reforms.sql.parser.SqlWords.*;

/**
 *
 * @author evgenie
 */
public class OptWords {

    public static final OptWord OW_O_NOT = new OptWord(false, SW_NOT);

    public static final OptWord OW_O_OUTER = new OptWord(false, SW_OUTER);

    public static final OptWord OW_R_IN = new OptWord(true, SW_IN);

    public static final OptWord OW_R_LIKE = new OptWord(true, SW_LIKE);

    public static final OptWord OW_R_BETWEEN = new OptWord(true, SW_BETWEEN);

    public static final OptWord OW_R_GROUP = new OptWord(true, SW_GROUP);

    public static final OptWord OW_R_BY = new OptWord(true, SW_BY);

    public static final OptWord OW_R_ORDER = new OptWord(true, SW_ORDER);

    public static final OptWord OW_R_EXISTS = new OptWord(true, SW_EXISTS);

    public static final OptWord OW_R_INNER = new OptWord(true, SW_INNER);

    public static final OptWord OW_R_JOIN = new OptWord(true, SW_JOIN);

    public static final OptWord OW_R_CROSS = new OptWord(true, SW_CROSS);

    public static final OptWord OW_R_FULL = new OptWord(true, SW_FULL);

    public static final OptWord OW_R_LEFT = new OptWord(true, SW_LEFT);

    public static final OptWord OW_R_RIGHT = new OptWord(true, SW_RIGHT);

}

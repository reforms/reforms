package com.reforms.orm.select;

import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.ColumnAliasParser;
import com.reforms.orm.dao.column.ColumnAliasType;

import org.junit.Test;

import static com.reforms.orm.dao.column.ColumnAliasType.*;
import static org.junit.Assert.assertEquals;

/**
 * Тесты парсера
 * @author evgenie
 */
public class UTestColumnAliasParser {

    @Test
    public void testColumnAliasParser_DataTypeTesting() {
        // positive
        assertColumnAlias("z#A", CAT_Z_BOOLEAN, "A", null);
        assertColumnAlias("z1#A", CAT_Z_BOOLEAN, "A", "1");
        assertColumnAlias("y#A", CAT_Y_BYTE, "A", null);
        assertColumnAlias("x#A", CAT_X_SHORT, "A", null);
        assertColumnAlias("i#A", CAT_I_INT, "A", null);
        assertColumnAlias("i1#A", CAT_I_INT, "A", "1");
        assertColumnAlias("f#A", CAT_F_FLOAT, "A", null);
        assertColumnAlias("w#A", CAT_W_DOUBLE, "A", null);
        assertColumnAlias("l#A", CAT_L_LONG, "A", null);
        assertColumnAlias("s#A", CAT_S_STRING, "A", null);
        assertColumnAlias("A", null, "A", null);
        assertColumnAlias("n#A", CAT_N_BIGDECIMAL, "A", null);
        assertColumnAlias("n2#A", CAT_N_BIGDECIMAL, "A", "2");
        assertColumnAlias("I10#A", CAT_I_BIGINTEGER, "A", "10");
        assertColumnAlias("n20#A", CAT_N_BIGDECIMAL, "A", "20");
        assertColumnAlias("d#A", CAT_D_DATE, "A", null);
        assertColumnAlias("d1#A", CAT_D_DATE, "A", "1");
        assertColumnAlias("v#A", CAT_V_TIME, "A", null);
        assertColumnAlias("t#A", CAT_T_TIMESTAMP, "A", null);
        assertColumnAlias("a#A", CAT_A_ASCII_STREAM, "A", null);
        assertColumnAlias("b#A", CAT_B_BINARY_STREAM, "A", null);
        assertColumnAlias("u1#A", CAT_U_CUSTOM_TYPE, "A", "1");
        assertColumnAlias("u11#A", CAT_U_CUSTOM_TYPE, "A", "11");
        assertColumnAlias("u111#A", CAT_U_CUSTOM_TYPE, "A", "111");
        // negate
        assertColumnAlias("a_", null, "a_", null);
    }

    @Test
    public void testColumnAliasParser_FullTesting() {
        assertColumnAlias("bobj1.bobj2.bobj3", null, "bobj1.bobj2.bobj3", null, null);
        assertColumnAlias("b3:t#bobj1.bobj2.bobj3", CAT_T_TIMESTAMP, "bobj1.bobj2.bobj3", null, "b3");
        assertColumnAlias("b3:", null, null, null, "b3");
        assertColumnAlias("t#bobj1.bobj2.bobj3", CAT_T_TIMESTAMP, "bobj1.bobj2.bobj3", null, null);
        assertColumnAlias("b3:t#bobj1.bobj2.bobj3", CAT_T_TIMESTAMP, "bobj1.bobj2.bobj3", null, "b3");
        assertColumnAlias("b3:t#", CAT_T_TIMESTAMP, null, null, "b3");
        assertColumnAlias("t#", CAT_T_TIMESTAMP, null, null, null);
        // TODO разобраться
        assertColumnAlias("b3:?", null, "?", null, "b3");
        assertColumnAlias("b3:!", null, "!", null, "b3");
        assertColumnAlias("!", null, "!", null, null);
    }

    private void assertColumnAlias(String expectedAlias, ColumnAliasType expectedAliasType, String expectedFieldName, Object expecetedExtra) {
        assertColumnAlias(expectedAlias, expectedAliasType, expectedFieldName, expecetedExtra, null);
    }

    private void assertColumnAlias(String expectedAlias, ColumnAliasType expectedAliasType, String expectedFieldName, Object expecetedExtra,
            String expectedSqlAlias) {
        ColumnAliasParser parser = new ColumnAliasParser();
        ColumnAlias cAlias = parser.parseColumnAlias(expectedAlias);
        assertEquals(expectedAlias, cAlias.getAlias());
        assertEquals(expectedAliasType, cAlias.getAliasType());
        assertEquals(expectedFieldName, cAlias.getJavaAliasKey());
        assertEquals(expecetedExtra, cAlias.getExtra());
        assertEquals(expectedSqlAlias, cAlias.getSqlAliasKey());
    }
}
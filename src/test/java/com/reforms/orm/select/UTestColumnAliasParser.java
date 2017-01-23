package com.reforms.orm.select;

import static com.reforms.orm.select.ColumnAliasType.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.ColumnAliasParser;
import com.reforms.orm.select.ColumnAliasType;

public class UTestColumnAliasParser {

    @Test
    public void testColumnAliasParser() {
        // positive
        assertColumnAlias("z_A", CAT_Z_BOOLEAN, "A", null);
        assertColumnAlias("z1_A", CAT_Z_BOOLEAN, "A", "1");
        assertColumnAlias("y_A", CAT_Y_BYTE, "A", null);
        assertColumnAlias("x_A", CAT_X_SHORT, "A", null);
        assertColumnAlias("i_A", CAT_I_INT, "A", null);
        assertColumnAlias("i1_A", CAT_I_INT, "A", "1");
        assertColumnAlias("f_A", CAT_F_FLOAT, "A", null);
        assertColumnAlias("w_A", CAT_W_DOUBLE, "A", null);
        assertColumnAlias("l_A", CAT_L_LONG, "A", null);
        assertColumnAlias("s_A", CAT_S_STRING, "A", null);
        assertColumnAlias("A", CAT_S_STRING, "A", null);
        assertColumnAlias("n_A", CAT_N_BIGDECIMAL, "A", null);
        assertColumnAlias("n2_A", CAT_N_BIGDECIMAL, "A", "2");
        assertColumnAlias("n20_A", CAT_N_BIGDECIMAL, "A", "20");
        assertColumnAlias("d_A", CAT_D_DATE, "A", null);
        assertColumnAlias("d1_A", CAT_D_DATE, "A", "1");
        assertColumnAlias("v_A", CAT_V_TIME, "A", null);
        assertColumnAlias("t_A", CAT_T_TIMESTAMP, "A", null);
        assertColumnAlias("a_A", CAT_A_ASCII_STREAM, "A", null);
        assertColumnAlias("b_A", CAT_B_BINARY_STREAM, "A", null);
        assertColumnAlias("u1_A", CAT_U_CUSTOM_TYPE, "A", "1");
        assertColumnAlias("u11_A", CAT_U_CUSTOM_TYPE, "A", "11");
        assertColumnAlias("u111_A", CAT_U_CUSTOM_TYPE, "A", "111");
        // negate
        assertColumnAlias("a_", CAT_S_STRING, "a_", null);
    }

    private void assertColumnAlias(String expectedAlias, ColumnAliasType expectedAliasType, String expectedAliasKey, Object expecetedExtra) {
        ColumnAliasParser parser = new ColumnAliasParser();
        ColumnAlias cAlias = parser.parseColumnAlias(expectedAlias);
        assertEquals(expectedAlias, cAlias.getAlias());
        assertEquals(expectedAliasType, cAlias.getAliasType());
        assertEquals(expectedAliasKey, cAlias.getAliasKey());
        assertEquals(expecetedExtra, cAlias.getExtra());
    }
}
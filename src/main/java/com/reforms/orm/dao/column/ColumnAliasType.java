package com.reforms.orm.dao.column;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * *  Список поддерживаемых форматов для алиасов:
 *  z               - boolean
 *  y               - byte
 *  x               - short
 *  i               - int
 *  f               - float
 *  w               - double
 *  l               - long
 *  e               - java.lang.Enum
 *  s or nothing    - java.lang.String
 *  n               - java.math.BigDecimal
 *  I               - java.math.BigInteger
 *  d               - java.sql.Date
 *  v               - java.sql.Time
 *  t               - java.sql.Timestamp
 *  a               - java.io.InputStream as AsciiStream
 *  b               - java.io.InputStream as BinaryStream
 *  u               - User Custome Type, need Registry IReportValueConverter
 * @author evgenie
 *
 */
public enum ColumnAliasType {

    CAT_Z_BOOLEAN("z"),
    CAT_Y_BYTE("y"),
    CAT_X_SHORT("x"),
    CAT_I_INT("i"),
    CAT_F_FLOAT("f"),
    CAT_W_DOUBLE("w"),
    CAT_L_LONG("l"),
    CAT_E_ENUM("e"),
    CAT_S_STRING("s"),
    CAT_N_BIGDECIMAL("n"),
    CAT_I_BIGINTEGER("I"),
    CAT_D_DATE("d"),
    CAT_V_TIME("v"),
    CAT_T_TIMESTAMP("t"),
    CAT_A_ASCII_STREAM("a"),
    CAT_B_BINARY_STREAM("b"),
    CAT_U_CUSTOM_TYPE("u");

    private static Map<String, ColumnAliasType> MARKER2TYPES = init();

    private static Map<String, ColumnAliasType> init() {
        Map<String, ColumnAliasType> m2types = new HashMap<>();
        for (ColumnAliasType type : values()) {
            m2types.put(type.getMarker(), type);
        }
        return Collections.unmodifiableMap(m2types);
    }

    private final String marker;

    ColumnAliasType(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }

    public static ColumnAliasType getType(char marker) {
        ColumnAliasType cAliasType = MARKER2TYPES.get(String.valueOf(marker));
        if (cAliasType == null) {
            return CAT_U_CUSTOM_TYPE;
        }
        return cAliasType;
    }

    public static ColumnAliasType getType(String marker) {
        return MARKER2TYPES.get(marker);
    }

}

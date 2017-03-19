package com.reforms.orm.dao.bobj.reader;

import static com.reforms.orm.dao.column.ColumnAliasType.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResultSetValueReaderFactory {

    private Map<Object, IResultSetValueReader<?>> baseConverters;

    private Map<Object, IResultSetValueReader<?>> customConverters;

    public ResultSetValueReaderFactory configure() {
        baseConverters = new HashMap<>();
        addParamRsReader(new BooleanResultSetValueReader(), CAT_Z_BOOLEAN.getMarker(), Boolean.class, boolean.class);
        addParamRsReader(new ByteResultSetValueReader(), CAT_Y_BYTE.getMarker(), Byte.class, byte.class);
        addParamRsReader(new ShortResultSetValueReader(), CAT_X_SHORT.getMarker(), Short.class, short.class);
        addParamRsReader(new IntResultSetValueReader(), CAT_I_INT.getMarker(), Integer.class, int.class);
        addParamRsReader(new FloatResultSetValueReader(), CAT_F_FLOAT.getMarker(), Float.class, float.class);
        addParamRsReader(new DoubleResultSetValueReader(), CAT_W_DOUBLE.getMarker(), Double.class, double.class);
        addParamRsReader(new LongResultSetValueReader(), CAT_L_LONG.getMarker(), Long.class, long.class);
        addParamRsReader(new EnumResultSetValueReader(this), CAT_E_ENUM.getMarker(), Enum.class);
        addParamRsReader(new StringResultSetValueReader(), CAT_S_STRING.getMarker(), String.class);
        addParamRsReader(new BigDecimalResultSetValueReader(), CAT_N_BIGDECIMAL.getMarker(), BigDecimal.class);
        addParamRsReader(new DateResultSetValueReader(), CAT_D_DATE.getMarker(), java.sql.Date.class, java.util.Date.class);
        addParamRsReader(new TimestampResultSetValueReader(), CAT_T_TIMESTAMP.getMarker(), Timestamp.class);
        addParamRsReader(new TimeResultSetValueReader(), CAT_V_TIME.getMarker(), Time.class);
        addParamRsReader(new AsciiStreamResultSetValueReader(), CAT_A_ASCII_STREAM.getMarker());
        addParamRsReader(new BinaryStreamResultSetValueReader(), CAT_B_BINARY_STREAM.getMarker(), byte[].class);
        return this;
    }

    private void addParamRsReader(IResultSetValueReader<?> converter, Object... keys) {
        for (Object key : keys) {
            baseConverters.put(key, converter);
        }
    }

    public ResultSetValueReaderFactory addCustomParamReader(IResultSetValueReader<?> converter, Object... keys) {
        if (customConverters == null) {
            customConverters = new HashMap<>();
        }
        for (Object key : keys) {
            customConverters.put(key, converter);
        }
        return this;
    }

    public ResultSetValueReaderFactory sealed() {
        if (baseConverters == null) {
            baseConverters = new HashMap<>();
        }
        baseConverters = Collections.unmodifiableMap(baseConverters);
        return this;
    }

    public ResultSetValueReaderFactory sealedCustom() {
        if (customConverters == null) {
            customConverters = new HashMap<>();
            customConverters = Collections.unmodifiableMap(customConverters);
        }
        return this;
    }

    public IResultSetValueReader<?> getParamRsReader(Object key) {
        if (customConverters != null) {
            IResultSetValueReader<?> customConverter = customConverters.get(key);
            if (customConverter != null) {
                return customConverter;
            }
        }
        if (baseConverters == null) {
            throw new IllegalStateException("Необходимо сконфигурировать 'ParamRsReaderFactory'");
        }
        return baseConverters.get(key);
    }
}

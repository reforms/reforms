package com.reforms.orm.select.bobj.reader;

import static com.reforms.orm.select.ColumnAliasType.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParamRsReaderFactory {

    private Map<Object, IParamRsReader<?>> baseConverters;

    private Map<Object, IParamRsReader<?>> customConverters;

    public ParamRsReaderFactory configure() {
        baseConverters = new HashMap<>();
        addParamRsReader(new BooleanParamRsReader(), CAT_Z_BOOLEAN.getMarker(), Boolean.class, boolean.class);
        addParamRsReader(new ByteParamRsReader(), CAT_Y_BYTE.getMarker(), Byte.class, byte.class);
        addParamRsReader(new ShortParamRsReader(), CAT_X_SHORT.getMarker(), Short.class, short.class);
        addParamRsReader(new IntParamRsReader(), CAT_I_INT.getMarker(), Integer.class, int.class);
        addParamRsReader(new FloatParamRsReader(), CAT_F_FLOAT.getMarker(), Float.class, float.class);
        addParamRsReader(new DoubleParamRsReader(), CAT_W_DOUBLE.getMarker(), Double.class, double.class);
        addParamRsReader(new LongParamRsReader(), CAT_L_LONG.getMarker(), Long.class, long.class);
        addParamRsReader(new StringParamRsReader(), CAT_S_STRING.getMarker(), String.class);
        addParamRsReader(new BigDecimalParamRsReader(), CAT_N_BIGDECIMAL.getMarker(), BigDecimal.class);
        addParamRsReader(new DateParamRsReader(), CAT_D_DATE.getMarker(), java.sql.Date.class, java.util.Date.class);
        addParamRsReader(new TimestampParamRsReader(), CAT_T_TIMESTAMP.getMarker(), Timestamp.class);
        addParamRsReader(new TimeColumnValueConverter(), CAT_V_TIME.getMarker(), Time.class);
        addParamRsReader(new AsciiStreamParamRsReader(), CAT_A_ASCII_STREAM.getMarker());
        addParamRsReader(new BinaryStreamParamRsReader(), CAT_B_BINARY_STREAM.getMarker(), byte[].class);
        return this;
    }

    private void addParamRsReader(IParamRsReader<?> converter, Object... keys) {
        for (Object key : keys) {
            baseConverters.put(key, converter);
        }
    }

    public ParamRsReaderFactory addCustomParamRsReader(IParamRsReader<?> converter, Object... keys) {
        if (customConverters == null) {
            customConverters = new HashMap<>();
        }
        for (Object key : keys) {
            customConverters.put(key, converter);
        }
        return this;
    }

    public ParamRsReaderFactory sealed() {
        if (baseConverters == null) {
            baseConverters = new HashMap<>();
        }
        baseConverters = Collections.unmodifiableMap(baseConverters);
        if (customConverters == null) {
            customConverters = new HashMap<>();
        }
        customConverters = Collections.unmodifiableMap(customConverters);
        return this;
    }

    public IParamRsReader<?> getParamRsReader(Object key) {
        if (customConverters != null) {
            IParamRsReader<?> customConverter = customConverters.get(key);
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

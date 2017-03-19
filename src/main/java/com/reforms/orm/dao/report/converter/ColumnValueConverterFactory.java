package com.reforms.orm.dao.report.converter;

import static com.reforms.orm.dao.column.ColumnAliasType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.reforms.orm.ConverterConfig;
import com.reforms.orm.dao.report.tl.DateTimeFormatterTl;
import com.reforms.orm.dao.report.tl.NumberFormatterTl;

public class ColumnValueConverterFactory {

    private Map<String, IColumnValueConverter> baseConverters;

    private Map<String, IColumnValueConverter> customConverters;

    public ColumnValueConverterFactory configure(ConverterConfig config) {
        baseConverters = new HashMap<>();
        baseConverters.put(CAT_Z_BOOLEAN.getMarker(), new BooleanColumnValueConverter());
        baseConverters.put(CAT_Y_BYTE.getMarker(), new ByteColumnValueConverter());
        baseConverters.put(CAT_X_SHORT.getMarker(), new ShortColumnValueConverter());
        baseConverters.put(CAT_I_INT.getMarker(), new IntColumnValueConverter());
        baseConverters.put(CAT_F_FLOAT.getMarker(), new FloatColumnValueConverter());
        baseConverters.put(CAT_W_DOUBLE.getMarker(), new DoubleColumnValueConverter());
        baseConverters.put(CAT_L_LONG.getMarker(), new LongColumnValueConverter());
        baseConverters.put(CAT_S_STRING.getMarker(), new StringColumnValueConverter());
        NumberFormatterTl tlNumberFormatter = new NumberFormatterTl(config.getNumberPattern());
        baseConverters.put(CAT_N_BIGDECIMAL.getMarker(), new BigDecimalColumnValueConverter(tlNumberFormatter));
        DateTimeFormatterTl tlDateFormatter = new DateTimeFormatterTl(config.getDatePattern());
        baseConverters.put(CAT_D_DATE.getMarker(), new DateColumnValueConverter(tlDateFormatter));
        DateTimeFormatterTl tlTimestampFormatter = new DateTimeFormatterTl(config.getTimestampPattern());
        baseConverters.put(CAT_T_TIMESTAMP.getMarker(), new TimestampColumnValueConverter(tlTimestampFormatter));
        DateTimeFormatterTl tlTimeFormatter = new DateTimeFormatterTl(config.getTimePattern());
        baseConverters.put(CAT_V_TIME.getMarker(), new TimeColumnValueConverter(tlTimeFormatter));
        String asciiEncoding = config.getAsciiEncoding();
        baseConverters.put(CAT_A_ASCII_STREAM.getMarker(), new AsciiStreamColumnValueConverter(asciiEncoding));
        String binaryEncoding = config.getBinaryEncoding();
        baseConverters.put(CAT_B_BINARY_STREAM.getMarker(), new BinaryStreamColumnValueConverter(binaryEncoding));
        return this;
    }

    public ColumnValueConverterFactory addCustomConverter(String key, IColumnValueConverter converter) {
        if (customConverters == null) {
            customConverters = new HashMap<>();
        }
        customConverters.put(key, converter);
        return this;
    }

    public ColumnValueConverterFactory sealed() {
        if (baseConverters == null) {
            baseConverters = new HashMap<>();
        }
        baseConverters = Collections.unmodifiableMap(baseConverters);
        return this;
    }

    public ColumnValueConverterFactory sealedCustom() {
        if (customConverters == null) {
            customConverters = new HashMap<>();
            customConverters = Collections.unmodifiableMap(customConverters);
        }
        return this;
    }

    public IColumnValueConverter getConverter(String key) {
        if (customConverters != null) {
            IColumnValueConverter customConverter = customConverters.get(key);
            if (customConverter != null) {
                return customConverter;
            }
        }
        if (baseConverters == null) {
            throw new IllegalStateException("Необходимо сконфигурировать 'ColumnValueConverterFactory'");
        }
        return baseConverters.get(key);
    }
}

package com.reforms.orm.select;

import static com.reforms.orm.select.ColumnAliasType.*;

/**
 *  * Пример:
 *  SELECT tableName.id AS lID
 *
 *  имеем на вход 'l_ID':
 *      alias = l_ID
 *      aliasType = CAT_LONG -  потому что префикс l
 *      aliasKey = ID
 *
 *  Формат: 'format_type[x]_ALIAS_NAME', где -
 *      format_type - описание формата,
 *      x           - числовое значение, не обязательный параметр
 *      _           - нижнее подчеркивание - разделитель - обязательный критерий того, что фильтр задан.
 *      ALIAS_NAME  - наименование колонки (рекомендованно указывать в верхнем регистре)
 *
 *  Список поддерживаемых форматов для алиасов:
 *  z               - boolean
 *  y               - byte
 *  z               - short
 *  i               - int
 *  f               - float
 *  w               - double
 *  l               - long
 *  s or nothing    - java.lang.String
 *  n               - java.math.BigDecimal
 *  d               - java.sql.Date
 *  v               - java.sql.Time
 *  t               - java.sql.Timestamp
 *  a               - java.io.InputStream as AsciiStream
 *  b               - java.io.InputStream as BinaryStream
 *  u               - User Custome Type, need Registry IReportValueConverter
 *
 * @author evgenie
 */
public class ColumnAliasParser {

    public ColumnAlias parseColumnAlias(String alias) {
        if (alias == null || alias.isEmpty()) {
            return null;
        }
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(alias);
        cAlias.setAliasType(CAT_S_STRING);
        cAlias.setAliasKey(alias);
        cAlias.setAliasPrefix(CAT_S_STRING.getMarker());

        String formatType = String.valueOf(alias.charAt(0));
        ColumnAliasType cType = ColumnAliasType.getType(formatType);
        if (cType != null) {
            int keyStartIndex = 1;
            if (alias.length() > 1) {
                String aliasPrefix = cType.getMarker();
                String xIntValue = parseNumberValue(alias);
                if (xIntValue != null) {
                    cAlias.setExtra(xIntValue);
                    keyStartIndex += xIntValue.length();
                    aliasPrefix += xIntValue;
                }
                int underscoreIndex = aliasPrefix.length();
                if (alias.length() > underscoreIndex + 1 && '_' == alias.charAt(underscoreIndex)) {
                    String aliasKey = alias.substring(keyStartIndex + 1);
                    cAlias.setAliasKey(aliasKey);
                    cAlias.setAliasPrefix(aliasPrefix);
                    cAlias.setAliasType(cType);
                }
            }
        }
        return cAlias;
    }

    private String parseNumberValue(String alias) {
        int index = 1;
        while (index < alias.length() && Character.isDigit(alias.charAt(index))) {
            index++;
        }
        return index == 1 ? null : alias.substring(1, index);
    }

}

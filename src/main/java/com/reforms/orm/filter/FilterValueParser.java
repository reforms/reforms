package com.reforms.orm.filter;

import static com.reforms.orm.select.ColumnAliasType.*;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.ColumnAliasType;

/**
 *  * Пример:
 *  SELECT tableName.id FROM goods WHERE id = :l_id
 *
 *  имеем на вход 'l_id':
 *        на выходе = id типа CAT_LONG -  потому что префикс l и имеется нижнее подчеркивание
 *
 *  Формат: 'format_type[x]_FILTER_NAME', где -
 *      format_type - описание формата,
 *      x           - числовое значение, не обязательный параметр
 *      _           - нижнее подчеркивание - разделитель - обязательный критерий того, что фильтр задан.
 *      FILTER_NAME  - наименование фильтра
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
 *  u               - User Custome Type
 *
 * @author evgenie
 */
public class FilterValueParser {

    public ColumnAlias parseFilterValue(String filterString) {
        if (filterString == null || filterString.isEmpty()) {
            return null;
        }
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(filterString);
        String formatType = String.valueOf(filterString.charAt(0));
        ColumnAliasType cType = ColumnAliasType.getType(formatType);
        if (cType != CAT_A_ASCII_STREAM && cType != CAT_B_BINARY_STREAM && cType != null) {
            int keyStartIndex = 1;
            if (filterString.length() > 1) {
                String aliasPrefix = cType.getMarker();
                String xIntValue = parseNumberValue(filterString);
                if (xIntValue != null) {
                    cAlias.setExtra(xIntValue);
                    keyStartIndex += xIntValue.length();
                    aliasPrefix += xIntValue;
                }
                int underscoreIndex = aliasPrefix.length();
                if (filterString.length() > underscoreIndex + 1 && '_' == filterString.charAt(underscoreIndex)) {
                    String aliasKey = filterString.substring(keyStartIndex + 1);
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

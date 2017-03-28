package com.reforms.orm.dao.column;

import com.reforms.ann.ThreadSafe;

/**
 *
 * ОБЩИЙ ФОРМАТ выражения после слова AS
 *
 * 'sql_alias:data_type#field_name', где
 * sql_alias  - часть, которая будет использоваться в sql и оставлена после преобразования запроса
 * data_type  - тип поля fieldName, по умолчанию String (s)
 * field_name - имя поля, куда устанавливать данные
 *
 *
 *
 *  Пример:
 *  SELECT tableName.id AS l#ID
 *
 *  имеем на вход 'l#ID':
 *      alias = l#ID
 *      aliasType = CAT_LONG -  потому что data_type - l
 *      aliasKey = ID
 *
 *  Формат: 'format_type[x]_ALIAS_NAME', где -
 *      data_type   - описание формата,
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
 * Примеры (все что после AS - мета-алиас):
 *  age AS bobj1.bobj2.bobj3
 *  age AS b3:t#bobj1.bobj2.bobj3
 *  age AS b3:
 *  age AS t#bobj1.bobj2.bobj3
 *  age AS b3:t#bobj1.bobj2.bobj3
 *  age AS b3:t#
 *  age AS t#
 *  ------- Особые случаи -----------
 *  age !   - выбор колонки игнорируется
 *
 * @author evgenie
 */
@ThreadSafe
public class ColumnAliasParser {

    /**
     * Распарсить алиас. Если filterFlag=true - то это признак того, что это фильтр, а не алиас
     * @param alias      алиас или фильтр
     * @param filterFlag признак того, что это фильтр
     * @return
     */
    public ColumnAlias parseColumnAlias(String alias) {
        if (alias == null || alias.isEmpty()) {
            return null;
        }
        String preparedAlias = alias;
        ColumnAlias cAlias = new ColumnAlias();
        cAlias.setAlias(alias);
        int colonIndex = preparedAlias.indexOf(':');
        if (colonIndex != -1) {
            String sqlAliasKey = preparedAlias.substring(0, colonIndex);
            preparedAlias = preparedAlias.substring(colonIndex + 1).trim();
            cAlias.setSqlAliasKey(sqlAliasKey);
        }
        if (!preparedAlias.isEmpty()) {
            ColumnAliasType aliasType = null;
            String aliasPrefix = null;
            String extra = null;
            int sharpIndex = preparedAlias.indexOf('#');
            if (sharpIndex != -1) {
                aliasPrefix = preparedAlias.substring(0, sharpIndex);
                if (aliasPrefix.length() < 1) {
                    throw new IllegalStateException("Указание пустого типа не допускается. Алиас '" + alias + "'");
                }
                char typeSymbol = preparedAlias.charAt(0);
                aliasType = ColumnAliasType.getType(typeSymbol);
                if (sharpIndex > 1) {
                    extra = preparedAlias.substring(1, sharpIndex);
                }
                preparedAlias = preparedAlias.substring(sharpIndex + 1);
                if (preparedAlias.isEmpty()) {
                    preparedAlias = null;
                }
            }
            cAlias.setJavaAliasKey(preparedAlias);
            cAlias.setExtra(extra);
            cAlias.setAliasPrefix(aliasPrefix);
            cAlias.setAliasType(aliasType);
        }
        return cAlias;
    }
}

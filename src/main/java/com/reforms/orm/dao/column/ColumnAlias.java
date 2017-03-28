package com.reforms.orm.dao.column;

/**
 * Пример:
 *  SELECT tableName.id AS lID
 *  имеем:
 *      alias = lID
 *      aliasType = CAT_LONG -  потому что префикс l
 *      aliasKey = ID
 *
 * Алиас, в котором есть вся информация для коректного вычитывания объекта из ResulSet и установки его DTO
 * Примеры (все что после AS - мета-алиас):
 *  age AS bobj1.bobj2.bobj3
 *  age AS b3:bobj1.bobj2.bobj3
 *  age AS b3:
 *  age AS t#bobj1.bobj2.bobj3
 *  age AS b3t:bobj1.bobj2.bobj3
 *  age AS b3:t#
 *  age AS t#
 * @author evgenie
 *
 */
public class ColumnAlias {

    /** Полное значение алиаса */
    private String alias;

    /** Тип, который явно указан в описании алиаса */
    private ColumnAliasType aliasType;

    /** Настоящий алиас для sql выражения, то-что должно быть после AS */
    private String sqlAliasKey;

    /** Значение алиаса без типа - fieldName или fieldName1.fieldName2.fieldNameN */
    private String javaAliasKey;

    /** Дополнительные параметры указанные в типе */
    private String extra;

    /** Тип и экстра вместе */
    private String aliasPrefix;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ColumnAliasType getAliasType() {
        return aliasType;
    }

    public void setAliasType(ColumnAliasType aliasType) {
        this.aliasType = aliasType;
    }

    public String getSqlAliasKey() {
        return sqlAliasKey;
    }

    public void setSqlAliasKey(String sqlAliasKey) {
        this.sqlAliasKey = sqlAliasKey;
    }

    public String getJavaAliasKey() {
        return javaAliasKey;
    }

    public boolean isQuestionAliasKey() {
        return "?".equals(javaAliasKey);
    }

    public void setJavaAliasKey(String javaAliasKey) {
        this.javaAliasKey = javaAliasKey;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getNumberValue() {
        if (extra != null) {
            return Integer.parseInt(extra);
        }
        return 0;
    }

    public String getAliasPrefix() {
        return aliasPrefix;
    }

    public void setAliasPrefix(String aliasPrefix) {
        this.aliasPrefix = aliasPrefix;
    }

    public boolean hasType() {
        return aliasType != null && alias != null && !alias.equals(javaAliasKey);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(alias).append("-").append(aliasType).append("-").append(sqlAliasKey).append("-")
                .append(javaAliasKey).append("-").append(extra).append("-").append(aliasPrefix);
        return builder.toString();
    }

}

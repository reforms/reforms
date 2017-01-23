package com.reforms.orm.select;

/**
 * Пример:
 *  SELECT tableName.id AS lID
 *  имеем:
 *      alias = lID
 *      aliasType = CAT_LONG -  потому что префикс l
 *      aliasKey = ID
 *
 * @author evgenie
 *
 */
public class ColumnAlias {

    private String alias;

    private ColumnAliasType aliasType;

    private String aliasKey;

    private String extra;

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

    public String getAliasKey() {
        return aliasKey;
    }

    public void setAliasKey(String aliasKey) {
        this.aliasKey = aliasKey;
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
        return aliasType != null && alias != null && !alias.equals(aliasKey);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(alias).append("-").append(aliasType).append("-")
                .append(aliasKey).append("-").append(extra).append("-").append(aliasPrefix);
        return builder.toString();
    }

}

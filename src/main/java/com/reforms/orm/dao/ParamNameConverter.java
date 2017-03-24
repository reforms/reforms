package com.reforms.orm.dao;

import static com.reforms.orm.dao.IParamNameType.PNT_BOBJ;

/**
 *
 * @author evgenie
 */
public class ParamNameConverter implements IParamNameConverter {

    @Override
    public String convertName(int valueType, String name) {
        if (PNT_BOBJ == valueType) {
            return convertColumnName(name);
        }
        return name;
    }

    protected String convertColumnName(String metaFieldName) {
        StringBuilder newFieldName = new StringBuilder(metaFieldName.length() + 1);
        boolean makeUpper = false;
        for (char symbol : metaFieldName.toCharArray()) {
            if ('_' == symbol) {
                makeUpper = true;
                continue;
            }
            if (makeUpper) {
                symbol = Character.toUpperCase(symbol);
                makeUpper = false;
            }
            newFieldName.append(symbol);
        }
        return newFieldName.toString();
    }
}
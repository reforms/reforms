package com.reforms.orm.dao.filter.param;

import static com.reforms.orm.dao.column.ColumnAliasType.*;

import java.util.Comparator;

import com.reforms.orm.dao.column.ColumnAliasType;

/**
 * Компаратор
 * @author evgenie
 */
class ParamSetterMarkerComparator implements Comparator<String> {

    ParamSetterMarkerComparator() {
    }

    @Override
    public int compare(String marker1, String marker2) {
        int priority1 = getPriority(marker1.charAt(0));
        int priority2 = getPriority(marker2.charAt(0));
        if (priority1 > priority2) {
            return 1;
        }
        if (priority1 < priority2) {
            return -1;
        }
        return marker1.compareTo(marker2);
    }

    private int getPriority(char symbol) {
        int index = 0;
        ColumnAliasType cat = getType(symbol);
        if (CAT_U_CUSTOM_TYPE == cat) {
            return index;
        }
        index++;
        if (CAT_A_ASCII_STREAM == cat) {
            return index;
        }
        index++;
        if (CAT_B_BINARY_STREAM == cat) {
            return index;
        }
        index++;
        if (CAT_N_BIGDECIMAL == cat) {
            return index;
        }
        index++;
        if (CAT_E_ENUM == cat) {
            return index;
        }
        index++;
        if (CAT_S_STRING == cat) {
            return index;
        }
        index++;
        if (CAT_T_TIMESTAMP == cat) {
            return index;
        }
        index++;
        if (CAT_W_DOUBLE == cat) {
            return index;
        }
        index++;
        if (CAT_F_FLOAT == cat) {
            return index;
        }
        index++;
        if (CAT_L_LONG == cat) {
            return index;
        }
        index++;
        if (CAT_D_DATE == cat) {
            return index;
        }
        index++;
        if (CAT_V_TIME == cat) {
            return index;
        }
        index++;
        if (CAT_I_INT== cat) {
            return index;
        }
        index++;
        if (CAT_X_SHORT == cat) {
            return index;
        }
        index++;
        if (CAT_Y_BYTE == cat) {
            return index;
        }
        index++;
        if (CAT_Z_BOOLEAN == cat) {
            return index;
        }
        return ++index;
    }

}

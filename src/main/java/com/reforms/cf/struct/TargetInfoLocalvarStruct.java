package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The localvar_target
 *
 * @author evgenie
 */
public class TargetInfoLocalvarStruct extends TargetInfoStruct {

    private int tableLength; // u2
    private LocalvarTableStruct[] tables;

    public TargetInfoLocalvarStruct(int targetType) {
        super(TargetInfos.LOCALVAR_TARGET_NAME, targetType);
    }

    public int getTableLength() {
        return tableLength;
    }

    public void setTableLength(int tableLength) {
        this.tableLength = tableLength;
    }

    public LocalvarTableStruct[] getTables() {
        return tables;
    }

    public void setTables(LocalvarTableStruct[] tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TargetInfo name=").append(getName()).append(" tableLength=").append(tableLength)
                .append(", tables=").append(Arrays.toString(tables));
        return builder.toString();
    }

}

package com.reforms.orm.scenario.enums;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

enum ClientMode {

    ONLINE("online"),
    OFFLINE("offline");

    @TargetField
    private String mode;

    ClientMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @TargetMethod
    public static ClientMode getClientMode(String mode) {
        return "online".equals(mode) ? ONLINE : OFFLINE;
    }

    @Override
    public String toString() {
        return mode;
    }

}

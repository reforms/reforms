package com.reforms.orm.scenario.enums;

import com.reforms.ann.TargetMethod;

enum ClientType {

    PERSON,
    COMPANY;

    @TargetMethod
    public static int getCode(ClientType clientType) {
        return PERSON == clientType ? 0 : 1;
    }

    @TargetMethod
    public static ClientType getClientType(int code) {
        return code == 0 ? PERSON : COMPANY;
    }
}

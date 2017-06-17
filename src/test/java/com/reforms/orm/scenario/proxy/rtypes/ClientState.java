package com.reforms.orm.scenario.proxy.rtypes;

import com.reforms.ann.TargetMethod;

public enum ClientState {

    ACTIVE(1),
    BLOCKED(2),
    DELETED(3);

    private int state;

    ClientState(int state) {
        this.state = state;
    }

    @TargetMethod
    public int getState() {
        return state;
    }

    @TargetMethod
    public static ClientState getClientState(int state) {
        return values()[state - 1];
    }
}
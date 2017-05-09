package com.reforms.orm.scenario.proxy;

import java.util.Date;

public class ClientFilter {

    private long clientId;

    private Date actTime;

    public ClientFilter(long clientId, Date actTime) {
        this.clientId = clientId;
        this.actTime = actTime;
    }

    public long getClientId() {
        return clientId;
    }

    public Date getActTime() {
        return actTime;
    }

}

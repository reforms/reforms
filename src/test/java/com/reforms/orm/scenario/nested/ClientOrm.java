package com.reforms.orm.scenario.nested;

import java.util.Date;

/**
 * Клиент
 * @author evgenie
 */
public class ClientOrm {

    private long clientId;

    private String clientName;

    private AddressOrm clientAddress;

    private Date logDate;

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public AddressOrm getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(AddressOrm clientAddress) {
        this.clientAddress = clientAddress;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    @Override
    public String toString() {
        return "[clientId=" + clientId + ", clientName=" + clientName + ", clientAddress=" + clientAddress + ", logDate=" + logDate + "]";
    }
}
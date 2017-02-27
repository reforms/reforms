package com.reforms.orm.scenario.enums;

/**
 * Клиент
 * @author evgenie
 */
public class ClientOrm {

    private long id;

    private ClientType type;

    private ClientState state;

    private ClientMode mode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClientType getType() {
        return type;
    }

    public void setType(ClientType type) {
        this.type = type;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public ClientMode getMode() {
        return mode;
    }

    public void setMode(ClientMode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[id=").append(id).append(", type=").append(type).append(", state=").append(state).append(", mode=")
                .append(mode).append("]");
        return builder.toString();
    }
}
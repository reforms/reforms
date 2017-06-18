package com.reforms.orm.scenario.proxy.batch;

import com.reforms.orm.scenario.proxy.rtypes.ClientState;

public class Client {

    private final int id;

    private final String name;

    private final ClientState state;

    public Client(int id, String name) {
        this(id, name, null);
    }

    public Client(int id, String name, ClientState state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ClientState getState() {
        return state;
    }

}

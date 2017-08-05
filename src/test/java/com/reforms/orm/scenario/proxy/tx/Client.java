package com.reforms.orm.scenario.proxy.tx;


public class Client {

    private final int id;

    private final String name;


    public Client(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

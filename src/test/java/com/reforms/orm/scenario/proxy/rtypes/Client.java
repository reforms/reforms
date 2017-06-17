package com.reforms.orm.scenario.proxy.rtypes;

public class Client {

    private Long id;

    private String name;

    public Client(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}

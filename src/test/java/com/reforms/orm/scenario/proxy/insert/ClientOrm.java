package com.reforms.orm.scenario.proxy.insert;

import java.io.Serializable;

/**
 * Клиент
 * @author evgenie
 */
public class ClientOrm implements Serializable {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

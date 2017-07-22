package com.reforms.orm.scenario.proxy.store_procedure;

import java.io.Serializable;

/**
 * Клиент
 * @author evgenie
 */
public class ClientOrm implements Serializable {

    private int id;

    private String name;

    public ClientOrm() {
    }

    public ClientOrm(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

package com.reforms.orm.scenario.simple;

import java.util.Date;

/**
 * Клиент
 * @author evgenie
 */
public class ClientOrm {

    private long id;

    private String name;

    private long addressId;

    private String city;

    private String street;

    private Date actTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Date getActTime() {
        return actTime;
    }

    public void setActTime(Date actTime) {
        this.actTime = actTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[id=").append(id).append(", name=").append(name).append(", addressId=").append(addressId).append(
                ", city=").append(city).append(", street=").append(street).append(", actTime=").append(actTime).append("]");
        return builder.toString();
    }


}
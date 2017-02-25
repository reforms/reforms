package com.reforms.orm.scenario.nested;

/**
 * Адрес
 * @author evgenie
 */
public class AddressOrm {

    private long addressId;

    private String refCity;

    private String refStreet;

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getRefCity() {
        return refCity;
    }

    public void setRefCity(String refCity) {
        this.refCity = refCity;
    }

    public String getRefStreet() {
        return refStreet;
    }

    public void setRefStreet(String refStreet) {
        this.refStreet = refStreet;
    }

    @Override
    public String toString() {
        return "[addressId=" + addressId + ", refCity=" + refCity + ", refStreet=" + refStreet + "]";
    }
}
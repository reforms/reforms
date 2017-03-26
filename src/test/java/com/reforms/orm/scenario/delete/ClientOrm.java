package com.reforms.orm.scenario.delete;

import java.util.Date;

/**
 * Клиент
 * @author evgenie
 */
class ClientOrm {

    private Long id;

    private String name;

    private Date actTime;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getActTime() {
        return actTime;
    }

    public void setActTime(Date actTime) {
        this.actTime = actTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[id=").append(id).append(", name=").append(name).append(", actTime=").append(actTime).
                append(", version=").append(version).append("]");
        return builder.toString();
    }
}
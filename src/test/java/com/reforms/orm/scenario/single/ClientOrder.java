package com.reforms.orm.scenario.single;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

/**
 *
 * @author evgenie
 */
public enum ClientOrder {
    FIRST(1L),
    SECOND(2L);

    @TargetField
    private long id;

    ClientOrder(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @TargetMethod
    public static ClientOrder getClientOrder(long id) {
        return 1L == id ? FIRST : SECOND;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
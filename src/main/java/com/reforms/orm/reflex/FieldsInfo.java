package com.reforms.orm.reflex;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

class FieldsInfo extends ArrayList<FieldInfo> {

    private Constructor<?> constructor;

    FieldsInfo(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }
}

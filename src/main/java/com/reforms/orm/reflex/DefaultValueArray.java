package com.reforms.orm.reflex;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author evgenie
 */
public class DefaultValueArray extends ArrayList<Object> {

    public int find(Object value, Class<?> clazzType) {
        for (int i = 0; i < size(); i++) {
            Object defaultValue = get(i);
            if (clazzType.isPrimitive()) {
                if (Objects.equals(value, defaultValue)) {
                    return i;
                }
            } else if (value == defaultValue) {
                return i;
            }
        }
        return -1;
    }
}

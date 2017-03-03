package com.reforms.orm.reflex;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertNotNull;

public class UTestDefaultValueCreator {

    @Test
    public void testSuccessCreate() throws Throwable {
        DefaultValueCreator creator = new DefaultValueCreator();
        assertNotNull(creator.createFirst(boolean.class));
        assertNotNull(creator.createFirst(Boolean.class));
        assertNotNull(creator.createFirst(byte.class));
        assertNotNull(creator.createFirst(Byte.class));
        assertNotNull(creator.createFirst(char.class));
        assertNotNull(creator.createFirst(Character.class));
        assertNotNull(creator.createFirst(short.class));
        assertNotNull(creator.createFirst(Short.class));
        assertNotNull(creator.createFirst(int.class));
        assertNotNull(creator.createFirst(Integer.class));
        assertNotNull(creator.createFirst(long.class));
        assertNotNull(creator.createFirst(Long.class));
        assertNotNull(creator.createFirst(double.class));
        assertNotNull(creator.createFirst(Double.class));
        assertNotNull(creator.createFirst(float.class));
        assertNotNull(creator.createFirst(Float.class));
        assertNotNull(creator.createFirst(String.class));
        assertNotNull(creator.createFirst(java.util.Date.class));
        assertNotNull(creator.createFirst(java.sql.Date.class));
        assertNotNull(creator.createFirst(java.sql.Time.class));
        assertNotNull(creator.createFirst(java.sql.Timestamp.class));
        assertNotNull(creator.createFirst(BigInteger.class));
        assertNotNull(creator.createFirst(BigDecimal.class));
        assertNotNull(creator.createFirst(Exception.class));
        assertNotNull(creator.createFirst(Error.class));
        assertNotNull(creator.createFirst(Collection.class));
        assertNotNull(creator.createFirst(Map.class));
        assertNotNull(creator.createFirst(List.class));
        assertNotNull(creator.createFirst(HashMap.class));
        assertNotNull(creator.createFirst(ArrayList.class));
        assertNotNull(creator.createFirst(BobjStrange.class));
    }

    @Test
    public void testCantCreate() {
        DefaultValueCreator creator = new DefaultValueCreator();
        for (Class<?> clazz : new Class<?>[] {Class.class, Enum.class, AbstractList.class, BobjRecurs2.class, BobjRecurs1.class}) {
            String message = null;
            try {
                creator.createFirst(clazz);
            } catch (Throwable e) {
                message = e.getMessage();
            }
            assertNotNull("Не должно быть возможности создать класс типа: '" + clazz + "'", message);
        }
    }
}

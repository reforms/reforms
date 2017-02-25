package com.reforms.orm;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import com.reforms.ann.ThreadSafe;

/**
 * Получение соединения из держателя
 * @author evgenie
 */
@ThreadSafe
public class ReflexConnectionHolder implements IConnectionHolder {

    public ReflexConnectionHolder() {
    }

    @Override
    public Connection getConnection(Object connectionHolder) throws Exception {
        if (connectionHolder == null) {
            throw new IllegalStateException("Объект, который удерживает соединение с БД не может быть null");
        }
        if (connectionHolder instanceof Connection) {
            return (Connection) connectionHolder;
        }
        if (connectionHolder instanceof DataSource) {
            return ((DataSource) connectionHolder).getConnection();
        }
        Class<?> clazz = connectionHolder.getClass();
        try {
            Method method = clazz.getMethod("getConnection");
            return (Connection) method.invoke(connectionHolder);
        } catch (Exception ex) {
            throw new IllegalStateException("Метод 'getConnection' не найден в классе '" + clazz + "'", ex);
        }
    }

}
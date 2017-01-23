package com.reforms.orm;

import java.sql.Connection;

import org.junit.Test;

import com.reforms.orm.ReflexConnectionHolder;

/**
 *
 * @author evgenie
 */
public class UTestReflexConnectionHolder {

    @Test
    public void runTest_GetConnection() throws Exception {
        ReflexConnectionHolder holder = new ReflexConnectionHolder();
        holder.getConnection(new Object() {
            public Connection getConnection() {
                return null;
            }
        });
    }
}

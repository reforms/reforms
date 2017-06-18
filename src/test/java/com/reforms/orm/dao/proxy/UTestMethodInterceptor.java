package com.reforms.orm.dao.proxy;

import com.reforms.orm.IOrmContext;
import com.reforms.orm.OrmDao;

import org.junit.Test;

import java.lang.reflect.Method;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static org.junit.Assert.assertEquals;

public class UTestMethodInterceptor {

    @Test
    public void testSimple() {
        IOrmContext ctx = getInstance(IOrmContext.class);
        IMethodInterceptor oldInterceptor = ctx.changeMethodInterceptor(current -> new LocalInterceptor());
        try {
            EmptyDao dao = OrmDao.createDao(null, EmptyDao.class);
            assertEquals(1, dao.checkDefaultInt());
            assertEquals("A", dao.checkDefaultString());
            assertEquals(25, dao.checkInt());
            assertEquals("A", dao.checkString());
        } finally {
            ctx.setMethodInterceptor(oldInterceptor);
        }
    }

    interface EmptyDao {

        default int checkDefaultInt() {
            return 1;
        }

        default String checkDefaultString() {
            return "B";
        }

        int checkInt();

        String checkString();

    };


    private static class LocalInterceptor implements IMethodInterceptor {

        @Override
        public boolean accept(Class<?> interfaze, Object connectionHolder, Object proxy, Method method, Object[] args) throws Exception {
            return method.getReturnType() == String.class || "checkInt".equals(method.getName());
        }

        @Override
        public Object invoke(Object connectionHolder, Object proxy, Method method, Object[] args) throws Throwable {
            if ("checkInt".equals(method.getName())) {
                return 25;
            }
            return "A";
        }

    }
}

package com.reforms.orm.dao.proxy;

import static com.reforms.ann.TargetQuery.*;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;
import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.bobj.IOrmDaoAdapter;

/**
 * Proxy implementations of dao
 * @author evgenie
 */
public class DaoProxy implements InvocationHandler {

    private static final Lookup TRUSTED_LOOKUP = getLookupField();

    private static Lookup getLookupField() {
        try {
            Field lookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookupField.setAccessible(true);
            return (Lookup) lookupField.get(null);
        } catch (Exception ex) {
            return null;
        }
    }

    private Object connectionHolder;
    private Class<?> daoInterface;

    public DaoProxy(Object connectionHolder, Class<?> daoInterface) {
        this.connectionHolder = connectionHolder;
        this.daoInterface = daoInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TargetQuery targetQuery = method.getAnnotation(TargetQuery.class);
        if (method.isDefault()) {
            return invokeDefaultMethod(proxy, method, args);
        }
        if (targetQuery != null) {
            if (QT_SELECT == targetQuery.type()) {
                return processSelectQuery(targetQuery, method, args);
            }
            if (QT_UPDATE == targetQuery.type()) {
                return processUpdateQuery(targetQuery, method, args);
            }
            if (QT_INSERT == targetQuery.type()) {
                return processInsertQuery(targetQuery, method, args);
            }
            if (QT_DELETE == targetQuery.type()) {
                return processDeleteQuery(targetQuery, method, args);
            }
        } else {
            if ("toString".equals(method.getName())) {
                return daoInterface.toString();
            }
            if ("equals".equals(method.getName())) {
                return proxy == args[0];
            }
            if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            }
            if (isCreateDaoMethod(method, args)) {
                return createDao(method, args);
            }
        }
        throw new IllegalStateException("Method '" + method + "' not implemented yet. Class '" + daoInterface + "'");
    }

    /**
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        if (TRUSTED_LOOKUP == null) {
            throw new IllegalStateException("'TRUSTED_LOOKUP' can not be initialized");
        }
        // Если не получить ссылку на этот лукапер,
        // вызывать метод возможно только для интерфейсов
        // которые объявленные в том же пакете что и вызов метода unreflectSpecial
        Object result = TRUSTED_LOOKUP
                .in(method.getDeclaringClass())
                .unreflectSpecial(method, method.getDeclaringClass())
                .bindTo(proxy)
                .invokeWithArguments(args);
        return result;
    }

    private Object processSelectQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, targetQuery.query());
        configureSelectAdapter(daoAdapter, method, args);
        Class<?> ormType = getOrmClass(targetQuery, method);
        if (Iterable.class.isAssignableFrom(method.getReturnType())) {
            return daoAdapter.loads(ormType);
        }
        return daoAdapter.load(ormType);
    }

    @SuppressWarnings("unchecked")
    private void configureSelectAdapter(IOrmDaoAdapter daoAdapter, Method method, Object[] args) {
        if (args.length == 0) {
            return;
        }
        for (int index = 0; index < args.length; index++) {
            Object argValue = args[index];
            TargetFilter filter = findTargetFilter(index, method);
            if (filter != null) {
                String filterName = filter.value();
                if (! filterName.isEmpty()) {
                    daoAdapter.addFilterPair(filterName, argValue);
                    continue;
                }
                daoAdapter.setFilterObject(argValue);
                continue;
            }
            if (argValue instanceof Map) {
                daoAdapter.addFilterPairs((Map<String, Object>) argValue);
                continue;
            }
            daoAdapter.addSimpleFilterValues(argValue);
        }
    }

    private Object processUpdateQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, targetQuery.query());
        configureUpdateAdapter(daoAdapter, method, args);
        return daoAdapter.update();
    }

    @SuppressWarnings("unchecked")
    private void configureUpdateAdapter(IOrmDaoAdapter daoAdapter, Method method, Object[] args) {
        if (args.length == 0) {
            return;
        }
        for (int index = 0; index < args.length; index++) {
            Object argValue = args[index];
            TargetFilter filter = findTargetFilter(index, method);
            if (filter != null) {
                String filterName = filter.value();
                if (! filterName.isEmpty()) {
                    daoAdapter.addUpdatePair(filterName, argValue);
                    continue;
                }
                daoAdapter.setUpdateObject(argValue);
                continue;
            }
            if (argValue instanceof Map) {
                daoAdapter.addUpdatePairs((Map<String, Object>) argValue);
                continue;
            }
            daoAdapter.addUpdateValue(argValue);
        }
    }

    private Object processInsertQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, targetQuery.query());
        configureInsertAdapter(daoAdapter, method, args);
        daoAdapter.insert();
        return null;
    }

    @SuppressWarnings("unchecked")
    private void configureInsertAdapter(IOrmDaoAdapter daoAdapter, Method method, Object[] args) {
        if (args.length == 0) {
            return;
        }
        for (int index = 0; index < args.length; index++) {
            Object argValue = args[index];
            TargetFilter filter = findTargetFilter(index, method);
            if (filter != null) {
                String filterName = filter.value();
                if (! filterName.isEmpty()) {
                    daoAdapter.addInsertPair(filterName, argValue);
                    continue;
                }
                daoAdapter.setInsertObject(argValue);
                continue;
            }
            if (argValue instanceof Map) {
                daoAdapter.addInsertPairs((Map<String, Object>) argValue);
                continue;
            }
            daoAdapter.addInsertValue(argValue);
        }
    }

    private Object processDeleteQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, targetQuery.query());
        configureSelectAdapter(daoAdapter, method, args);
        return daoAdapter.delete();
    }

    private TargetFilter findTargetFilter(int index, Method method) {
        Annotation[][] paramsAnns = method.getParameterAnnotations();
        if (paramsAnns.length > index) {
            for (Annotation paramAnn : paramsAnns[index]) {
                if (paramAnn instanceof TargetFilter) {
                    return (TargetFilter) paramAnn;
                }
            }
        }
        return null;
    }

    private Class<?> getOrmClass(TargetQuery targetQuery, Method method) {
        Class<?> ormType = targetQuery.orm();
        if (ormType != Object.class) {
            return ormType;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.isArray()) {
            return returnType.getComponentType();
        }
        return returnType;
    }

    private boolean isCreateDaoMethod(Method method, Object[] args) {
        Class<?> resultClass = method.getReturnType();
        Class<?>[] paramTypes = method.getParameterTypes();
        return resultClass.isInterface() && paramTypes.length == 0;
    }

    private Object createDao(Method method, Object[] args) {
        return OrmDao.createDao(connectionHolder, method.getReturnType());
    }

}

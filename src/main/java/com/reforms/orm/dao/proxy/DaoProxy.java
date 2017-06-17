package com.reforms.orm.dao.proxy;

import static com.reforms.ann.TargetQuery.*;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;
import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.bobj.IOrmDaoAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Proxy implementations of dao
 * @author evgenie
 */
public class DaoProxy implements InvocationHandler {

    /** Доверяет всем */
    private static final Lookup TRUSTED_LOOKUP = getLookupField();

    /** Пустой массив */
    private static final Object[] EMPTY_ARGS = new Object[]{};

    private static Lookup getLookupField() {
        try {
            Field lookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookupField.setAccessible(true);
            return (Lookup) lookupField.get(null);
        } catch (Exception ex) {
            return null;
        }
    }

    private final Object connectionHolder;
    private final Class<?> daoInterface;

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
        if (args == null) {
            args = EMPTY_ARGS;
        }
        if (targetQuery != null) {
            int queryType = targetQuery.type();
            if (QT_AUTO == queryType) {
                queryType = getQueryType(targetQuery);
            }
            if (QT_SELECT == queryType) {
                return processSelectQuery(targetQuery, method, args);
            }
            if (QT_UPDATE == queryType) {
                return processUpdateQuery(targetQuery, method, args);
            }
            if (QT_INSERT == queryType) {
                return processInsertQuery(targetQuery, method, args);
            }
            if (QT_DELETE == queryType) {
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

    private int getQueryType(TargetQuery targetQuery) {
        String query = getQuery(targetQuery);
        if (query.length() > 6) {
            String keyWord = new StringTokenizer(query, "(\n\t\b\f ", false).nextToken();
            if ("SELECT".equalsIgnoreCase(keyWord)) {
                return QT_SELECT;
            }
            if ("INSERT".equalsIgnoreCase(keyWord)) {
                return QT_INSERT;
            }
            if ("DELETE".equalsIgnoreCase(keyWord)) {
                return QT_DELETE;
            }
            if ("UPDATE".equalsIgnoreCase(keyWord)) {
                return QT_UPDATE;
            }
        }
        throw new IllegalStateException("Не удалось определить тип запроса в " + targetQuery);
    }

    @SuppressWarnings("unchecked")
    private Object processSelectQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, getQuery(targetQuery));
        boolean hasHandler = configureSelectAdapter(daoAdapter, method, args);
        Class<?> ormType = getOrmClass(targetQuery, method);
        // Обработка списков
        if (List.class.isAssignableFrom(method.getReturnType())) {
            return daoAdapter.loads(ormType);
        }
        // Обработка множества
        if (Set.class.isAssignableFrom(method.getReturnType())) {
            return daoAdapter.set(ormType);
        }
        // Обработка массивов
        if (method.getReturnType().isArray()) {
            List values = daoAdapter.loads(ormType);
            return values.toArray((Object[]) Array.newInstance(ormType, values.size()));
        }
        // Обработка итераторов
        if (OrmIterator.class.isAssignableFrom(method.getReturnType())) {
            return daoAdapter.iterate(ormType);
        }
        // Указан обработчик для выбираемых объектов
        if (hasHandler) {
            OrmHandler<Object> handler = findOrmHandler(args);
            if (handler == null) {
                throw new IllegalStateException("Ожидается OrmHandler, а получен null. " +
                        "Метод '" + method.getName() + "#" + method.getParameterTypes().length);
            }
            daoAdapter.handle((Class<Object>) ormType, handler);
            return null;
        }
        // Если ничего из перечисленных, значит одиночный объект
        return daoAdapter.load(ormType);
    }

    @SuppressWarnings("unchecked")
    private boolean configureSelectAdapter(IOrmDaoAdapter daoAdapter, Method method, Object[] args) {
        boolean hasHandler = false;
        if (args.length == 0) {
            return hasHandler;
        }
        for (int index = 0; index < args.length; index++) {
            Object argValue = args[index];
            // значение обработчика пропускаем
            if (argValue instanceof OrmHandler) {
                if (hasHandler) {
                    throw new IllegalStateException("Допускается указание только одного хендлера типа OrmHandler в параметрах метода. " +
                            "Метод '" + method.getName() + "#" + method.getParameterTypes().length);
                }
                hasHandler = true;
                continue;
            }
            TargetFilter filter = findTargetFilter(index, method);
            if (filter != null) {
                if (filter.columnFilter()) {
                    addSelectableFilter(daoAdapter, index, method, args);
                    continue;
                }
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
            if (argValue instanceof ISelectedColumnFilter) {
                daoAdapter.setSelectedColumnFilter(ISelectedColumnFilter.class.cast(argValue));
                continue;
            }
            daoAdapter.addSimpleFilterValues(argValue);
        }
        return hasHandler;
    }

    @SuppressWarnings("unchecked")
    private OrmHandler<Object> findOrmHandler(Object args[]) {
        for (Object arg : args) {
            if (arg instanceof OrmHandler) {
                return (OrmHandler<Object>) arg;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean addSelectableFilter(IOrmDaoAdapter daoAdapter, int index, Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();
        // добавляем массив индексов колонок на выборку данных
        if (paramTypes[index] == int[].class) {
            daoAdapter.addSelectableIndexes(int[].class.cast(args[index]));
            return true;
        }
        if (paramTypes[index] == Integer[].class) {
            for (Integer value : Integer[].class.cast(args[index])) {
                daoAdapter.addSelectableIndex(Integer.class.cast(value));
            }
        }
        // добавляем конкретную колонку на выборку данных
        if (paramTypes[index] == int.class) {
            daoAdapter.addSelectableIndex(Integer.class.cast(args[index]));
            return true;
        }
        if (paramTypes[index] == Integer.class && args[index] != null) {
            daoAdapter.addSelectableIndex(Integer.class.cast(args[index]));
            return true;
        }
        // добавляем коллекцию индексов колонок на выборку данных
        if (args[index] instanceof Collection) {
            for (Object value : (Collection<Object>) args[index]) {
                if (value instanceof Integer) {
                    daoAdapter.addSelectableIndex(Integer.class.cast(value));
                }
            }
            return true;
        }
        if (args[index] instanceof ISelectedColumnFilter) {
            daoAdapter.setSelectedColumnFilter(ISelectedColumnFilter.class.cast(args[index]));
            return true;
        }
        return false;
    }

    private Object processUpdateQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, getQuery(targetQuery));
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
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, getQuery(targetQuery));
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

    private String getQuery(TargetQuery targetQuery) {
        String query = targetQuery.query();
        if (query.isEmpty()) {
            query = targetQuery.value();
        }
        return query;
    }

    private Object processDeleteQuery(TargetQuery targetQuery, Method method, Object[] args) throws Exception {
        IOrmDaoAdapter daoAdapter = OrmDao.createDao(connectionHolder, getQuery(targetQuery));
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

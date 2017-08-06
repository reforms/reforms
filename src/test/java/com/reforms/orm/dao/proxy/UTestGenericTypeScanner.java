package com.reforms.orm.dao.proxy;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Тест на получения дженерика
 * @author evgenie
 */
public class UTestGenericTypeScanner {

    @Test
    public void test_getOrmTypeFromReturnCollectionGeneric() throws Exception {
        Method method_loadNames = GenericTypeScannerDao.class.getDeclaredMethod("loadNames");
        GenericTypeScanner scanner = new GenericTypeScanner(true, true);
        Class<?> clazz = scanner.getGenericWithCollection(method_loadNames);
        Assert.assertEquals(String.class, clazz);

        Method method_loadClients = GenericTypeScannerDao.class.getDeclaredMethod("loadClients");
        clazz = scanner.getGenericWithCollection(method_loadClients);
        Assert.assertEquals(ClientOrm.class, clazz);

        Method method_loadClients$1 = GenericTypeScannerDao.class.getDeclaredMethod("loadClients", String.class);
        clazz = scanner.getGenericWithCollection(method_loadClients$1);
        Assert.assertEquals(String.class, clazz);
    }

    @Test
    public void test_getOrmTypeFromReturnCollectionGeneric_innerClass() throws Exception {
        Method method_loadIds = InnerDao.class.getDeclaredMethod("loadIds");
        GenericTypeScanner scanner = new GenericTypeScanner(true, true);
        Class<?> clazz = scanner.getGenericWithCollection(method_loadIds);
        Assert.assertEquals(String.class, clazz);

        Method method_loadIdsMap = InnerDao.class.getDeclaredMethod("loadIdsMap");
        clazz = scanner.getGenericWithCollection(method_loadIdsMap);
        Assert.assertNull(clazz);
    }

    public interface InnerDao {

        public Set<String> loadIds();

        default Map<Long, String> loadIdsMap() {
            Map<Long, String> values = new HashMap<>();
            loadIds().forEach(value -> values.put(Long.valueOf(value), value));
            return values;
        }
    }
}

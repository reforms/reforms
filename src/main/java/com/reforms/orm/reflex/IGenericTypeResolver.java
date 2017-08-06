package com.reforms.orm.reflex;

import com.reforms.ann.TargetApi;

import java.lang.reflect.Method;

@TargetApi
public interface IGenericTypeResolver {

    /**
     * Возращает класс, указанный в дженерика
     * @param method метод, для которого нужно провернуть данную операцию
     * @return класс, указанный в дженерика или NULL, если не удалось выцепить
     */
    Class<?> getGenericWithCollection(Method method);
}

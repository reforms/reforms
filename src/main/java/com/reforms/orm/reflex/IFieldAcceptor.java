package com.reforms.orm.reflex;

import java.lang.reflect.Field;

/**
 * Контракт на акцепт поля или метода по своим критериям
 * @author evgenie
 */
public interface IFieldAcceptor {

    public boolean acceptField(Field field);

}

package com.reforms.orm.dao;

import java.sql.ResultSet;

/**
 * Контракт на вычитывание объекта из ResultSet
 * @author evgenie
 */
public interface IResultSetObjectReader {

    public boolean canRead(ResultSet rs) throws Exception;

    /**
     * Вычитытать объект из rs
     * @param rs источник данных
     * @return объект
     * @throws Exception ошибка при создании или вычитывании объекта
     */
    public <T> T read(ResultSet rs) throws Exception;
}
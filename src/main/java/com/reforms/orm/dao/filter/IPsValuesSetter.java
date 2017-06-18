package com.reforms.orm.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Контракт на установку значений в PS
 * @author evgenie
 */
public interface IPsValuesSetter {

    /**
     * Добавить значение
     * @param filterPrefix префикс
     * @param filterValue  значение
     * @return количество значений, которое было добавлено
     */
    int addFilterValue(String filterPrefix, Object filterValue);

    /**
     * Установить все накопленные значения в PS
     * @param ps подготовленный запрос
     * @return количество значений, которое было установлено
     * @throws SQLException ошибка
     */
    int setParamsTo(PreparedStatement ps) throws SQLException;
}

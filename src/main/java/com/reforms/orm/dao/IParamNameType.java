package com.reforms.orm.dao;

/**
 * Контракт на определение типа параметра
 * @author evgenie
 */
public interface IParamNameType {

    public static final int PNT_BOBJ = 0;

    public static final int PNT_MAP = 1;

    public static final int PNT_SEQUENCE = 2;

    public int getParamNameType();
}

package com.reforms.orm;

import org.junit.After;
import org.junit.Before;

public class GoodsDbTest extends DataDbTest {

    public GoodsDbTest(String dbName) {
        super(dbName);
    }

    private static final String CREATE_GOODS_TABLE_QUERY = "CREATE TABLE goods\n" +
            "(" +
            "  id bigint NOT NULL, " +
            "  name character varying(127) NOT NULL, " +
            "  description character varying(255) NOT NULL, " +
            "  price numeric(16,2) NOT NULL, " +
            "  articul character varying(20) NOT NULL, " +
            "  act_time timestamp NOT NULL," +
            "  CONSTRAINT pk_goods PRIMARY KEY (id)" +
            ")";

    private static final String INSERT_GOODS_QUERY_1 =
            "INSERT INTO goods (id, name,      description, price,   articul, act_time) "
                    + "VALUES          (1,  'Тапочки', 'Мягкие',    100.00,  'TR-75', {ts '2017-01-01 19:12:01.69'})";

    private static final String INSERT_GOODS_QUERY_2 =
            "INSERT INTO goods (id, name,      description, price,   articul, act_time) "
                    + "VALUES          (2,  'Подушки', 'Белые',    200.00,  'PR-75', {ts '2017-01-02 19:13:01.69'})";

    private static final String INSERT_GOODS_QUERY_3 =
            "INSERT INTO goods (id, name,      description, price,   articul, act_time) "
                    + "VALUES          (3,  'Одеяло', 'Пуховое',    300.00,  'ZR-75', {ts '2017-01-03 19:14:01.69'})";

    @Before
    public final void beforeTestReportDao() {
        invokeStatement(CREATE_GOODS_TABLE_QUERY);
        invokeStatement(INSERT_GOODS_QUERY_1);
        invokeStatement(INSERT_GOODS_QUERY_2);
        invokeStatement(INSERT_GOODS_QUERY_3);
    }

    @After
    public final void afterTestReportDao() {
        close();
    }
}

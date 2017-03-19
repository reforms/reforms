package com.reforms.orm.reflex;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.reflex.Reflexor;

/**
 * TODO тесты - добавить стресс-тесты
 * @author evgenie
 */
public class UTestReflexor {

    @Test
    public void runTest_CheckFields() {
        GoodsFieldFilter goodsFilter = new GoodsFieldFilter();
        goodsFilter.id = 1L;
        goodsFilter.name = "Тапочки";
        goodsFilter.price = new BigDecimal("100.00");
        goodsFilter.actTime = new Date();
        IReflexor reflexor = Reflexor.createReflexor(GoodsFieldFilter.class);
        assertEquals(goodsFilter.id, reflexor.getValue(goodsFilter, "id"));
        assertEquals(goodsFilter.name, reflexor.getValue(goodsFilter, "name"));
        assertEquals(goodsFilter.price, reflexor.getValue(goodsFilter, "price"));
        assertEquals(goodsFilter.actTime, reflexor.getValue(goodsFilter, "actTime"));
    }

    private static class GoodsFieldFilter {
        long id;
        String name;
        BigDecimal price;
        Date actTime;
    }

    @Test
    public void runTest_CheckSuperFields() {
        GoodsSuperFieldFilter goodsFilter = new GoodsSuperFieldFilter();
        goodsFilter.id = 1L;
        goodsFilter.name = "Тапочки";
        goodsFilter.price = new BigDecimal("100.00");
        goodsFilter.actTime = new Date();
        goodsFilter.discont = true;
        IReflexor reflexor = Reflexor.createReflexor(GoodsSuperFieldFilter.class);
        assertEquals(goodsFilter.id, reflexor.getValue(goodsFilter, "id"));
        assertEquals(goodsFilter.name, reflexor.getValue(goodsFilter, "name"));
        assertEquals(goodsFilter.price, reflexor.getValue(goodsFilter, "price"));
        assertEquals(goodsFilter.actTime, reflexor.getValue(goodsFilter, "actTime"));
        assertEquals(goodsFilter.discont, reflexor.getValue(goodsFilter, "discont"));
    }

    private static class GoodsSuperFieldFilter extends GoodsFieldFilter {
        boolean discont;
    }

    @Test
    public void runTest_CheckMethod() {
        GoodsMethodFilter goodsFilter = new GoodsMethodFilter().setId(1L).setName("Тапочки");
        goodsFilter = goodsFilter.setPrice(new BigDecimal("100.00")).setActTime(new Date());
        IReflexor reflexor = Reflexor.createReflexor(GoodsMethodFilter.class);
        assertEquals(goodsFilter.getId(), reflexor.getValue(goodsFilter, "id"));
        assertEquals(goodsFilter.getName(), reflexor.getValue(goodsFilter, "name"));
        assertEquals(goodsFilter.getPrice(), reflexor.getValue(goodsFilter, "price"));
        assertEquals(goodsFilter.getActTime(), reflexor.getValue(goodsFilter, "actTime"));
    }

    private static class GoodsMethodFilter {

        protected FilterMap data = new FilterMap();

        public long getId() {
            return (Long) data.get("id");
        }

        public GoodsMethodFilter setId(long id) {
            data.putValue("id", id);
            return this;
        }

        public String getName() {
            return (String) data.get("name");
        }

        public GoodsMethodFilter setName(String name) {
            data.putValue("name", name);
            return this;
        }

        public BigDecimal getPrice() {
            return (BigDecimal) data.get("price");
        }

        public GoodsMethodFilter setPrice(BigDecimal price) {
            data.putValue("price", price);
            return this;
        }

        public Date getActTime() {
            return (Date) data.get("actTime");
        }

        public GoodsMethodFilter setActTime(Date actTime) {
            data.putValue("actTime", actTime);
            return this;
        }

    }

    @Test
    public void runTest_CheckSuperMethod() {
        GoodsSuperMethodFilter goodsFilter = new GoodsSuperMethodFilter();
        goodsFilter.setDiscont(true).setId(1L).setName("Тапочки");
        goodsFilter.setPrice(new BigDecimal("100.00")).setActTime(new Date());
        IReflexor reflexor = Reflexor.createReflexor(GoodsSuperMethodFilter.class);
        assertEquals(goodsFilter.getId(), reflexor.getValue(goodsFilter, "id"));
        assertEquals(goodsFilter.getName(), reflexor.getValue(goodsFilter, "name"));
        assertEquals(goodsFilter.getPrice(), reflexor.getValue(goodsFilter, "price"));
        assertEquals(goodsFilter.getActTime(), reflexor.getValue(goodsFilter, "actTime"));
        assertEquals(goodsFilter.isDiscont(), reflexor.getValue(goodsFilter, "discont"));
    }

    private static class GoodsSuperMethodFilter extends GoodsMethodFilter {
        public boolean isDiscont() {
            return (Boolean) data.getValue("discont");
        }

        public GoodsSuperMethodFilter setDiscont(boolean discont) {
            data.putValue("discont", discont);
            return this;
        }
    }

    @Test
    public void runTest_CheckClassicFields() {
        GoodsClassicFilter goodsFilter = new GoodsClassicFilter().setId(1L).setName("Тапочки");
        goodsFilter = goodsFilter.setPrice(new BigDecimal("100.00")).setActTime(new Date());
        IReflexor reflexor = Reflexor.createReflexor(GoodsClassicFilter.class);
        assertEquals(goodsFilter.getId(), reflexor.getValue(goodsFilter, "id"));
        assertEquals(goodsFilter.getName(), reflexor.getValue(goodsFilter, "name"));
        assertEquals(goodsFilter.getPrice(), reflexor.getValue(goodsFilter, "price"));
        assertEquals(goodsFilter.getActTime(), reflexor.getValue(goodsFilter, "actTime"));
    }

    private static class GoodsClassicFilter {

        private long id;
        private String name;
        private BigDecimal price;
        private Date actTime;

        public long getId() {
            return id;
        }

        public GoodsClassicFilter setId(long id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public GoodsClassicFilter setName(String name) {
            this.name = name;
            return this;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public GoodsClassicFilter setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Date getActTime() {
            return actTime;
        }

        public GoodsClassicFilter setActTime(Date actTime) {
            this.actTime = actTime;
            return this;
        }

    }

    @Test
    public void runTest_CheckTypeResolving() {
        DeepObject1 do1 = new DeepObject1();
        assertOrmType(do1, "id", int.class);
        assertOrmType(do1, "do2", DeepObject2.class);
        assertOrmType(do1, "do2.name", String.class);
        assertOrmType(do1, "do2.do3", DeepObject3.class);
        assertOrmType(do1, "do2.do3.price", BigDecimal.class);
        assertOrmType(do1, "do2.do3.discont", boolean.class);
    }

    @Test
    public void runTest_CheckSetValue() throws Exception {
        IReflexor reflexor = Reflexor.createReflexor(DeepObject1.class);
        DeepObject1 do1 = (DeepObject1) reflexor.createInstance();
        reflexor.setValue(do1, "do2.do3.discont", true);
        assertEquals(true, do1.getDo2().getDo3().isDiscont());
        assertEquals(true, do1.getDo2().getDo3().discont);
    }

    private void assertOrmType(Object value, String metaFieldName, Class<?> expectedOrmClass) {
        IReflexor reflexor = Reflexor.createReflexor(value.getClass());
        assertEquals(expectedOrmClass, reflexor.getType(metaFieldName));
    }

    private static class DeepObject1 {
        private int id;

        private DeepObject2 do2;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public DeepObject2 getDo2() {
            return do2;
        }

        public void setDo2(DeepObject2 do2) {
            this.do2 = do2;
        }

    }

    private static class DeepObject2 {
        private String name;

        private DeepObject3 do3;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public DeepObject3 getDo3() {
            return do3;
        }

        public void setDo3(DeepObject3 do3) {
            this.do3 = do3;
        }

    }

    private static class DeepObject3 {
        private BigDecimal price;

        private boolean discont;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public boolean isDiscont() {
            return discont;
        }

        public void setDiscont(boolean discont) {
            this.discont = discont;
        }

    }
}

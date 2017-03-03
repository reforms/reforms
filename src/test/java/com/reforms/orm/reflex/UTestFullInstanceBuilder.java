package com.reforms.orm.reflex;

import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 * @author evgenie
 */
public class UTestFullInstanceBuilder {

    @Test
    public void testBuilder() throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(Bobj.class);
        builder.prepare();
        builder.append("flag", false);
        builder.append("flagBox", true);
        builder.append("bytic", (byte) 1);
        builder.append("byticBox", (byte) 2);
        builder.append("shortic", (short) 3);
        builder.append("shorticBox", (short) 4);
        builder.append("symbol", 'a');
        builder.append("symbolBox", 'b');
        builder.append("intic", 5);
        builder.append("inticBox", 6);
        builder.append("floatic", 7.0F);
        builder.append("floaticBox", 8.0F);
        builder.append("doublic", 9.0D);
        builder.append("doublicBox", 10.0D);
        builder.append("longic", 11L);
        builder.append("longicBox", 12L);
        builder.append("stringic", "13");
        builder.append("decimalic", new BigDecimal(14));
        builder.append("datic", new Date(0));
        builder.append("timic", new Time(0));
        builder.append("timestampic", new Timestamp(0));
        builder.append("enumic", BobjType.HARD);
        builder.append("bobjnnic.inticBox", 15);
        builder.append("bobjnnic.stringic", "16");
        String actualResult = builder.complete().toString();
        String expectedPattern = "\\[flag=false\\, " +
                "flagBox=true\\, " +
                "bytic=1\\, " +
                "byticBox=2\\, " +
                "shortic=3\\, " +
                "shorticBox=4\\, " +
                "symbol=a\\, " +
                "symbolBox=b\\, " +
                "intic=5\\, " +
                "inticBox=6\\, " +
                "floatic=7.0\\, " +
                "floaticBox=8.0\\, " +
                "doublic=9.0\\, " +
                "doublicBox=10.0\\, " +
                "longic=11\\, " +
                "longicBox=12\\, " +
                "stringic=13\\, " +
                "decimalic=14\\, " +
                "datic=[^,]+\\, " +
                "timic=[^,]+\\, " +
                "timestampic=[^,]+\\, " +
                "enumic=HARD\\, " +
                "bobjnnic=\\[inticBox=15\\, " +
                "stringic=16\\]\\]";
        assertTrue(actualResult, actualResult.matches(expectedPattern));
    }

    @Test
    public void testIntBuilder() throws Exception {
        for (int index = 0; index < 5 * 5 * 5 * 5 * 5; index++) {
            int int0 = index % 5, int1 = (index / 5) % 5, int2 = (index / 25) % 5, int3 = (index / 125) % 5, int4 = (index / 625) % 5;
            assertInt(int0, int1, int2, int3, int4);
        }
    }

    private void assertInt(int int0, int int1, int int2, int int3, int int4) throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(BobjInt.class);
        builder.prepare();
        builder.append("int0", int0);
        builder.append("int1", int1);
        builder.append("int2", int2);
        builder.append("int3", int3);
        builder.append("int4", int4);
        String actualResult = builder.complete().toString();
        assertEquals("[int0=" + int0 + ", int1=" + int1 + ", int2=" + int2 + ", int3=" + int3 + ", int4=" + int4 + "]", actualResult);
    }

    @Test
    public void testBooleanBuilder() throws Exception {
        int count = Integer.parseInt("111111", 2);
        for (int index = 0; index < count; index++) {
            boolean bool0 = (index & 1) == 1, bool1 = (index & 2) == 2, boolF = (index & 4) == 4, boolT = (index & 8) == 8, bool2 = (index & 16) == 16, bool3 = (index & 32) == 32;
            assertBoolean(bool0, bool1, boolF, boolT, bool2, bool3);
        }
    }

    private void assertBoolean(boolean bool0, boolean bool1, boolean boolF, boolean boolT, boolean bool2, boolean bool3) throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(BobjBoolean.class);
        builder.prepare();
        builder.append("bool0", bool0);
        builder.append("bool1", bool1);
        builder.append("boolF", boolF);
        builder.append("boolT", boolT);
        builder.append("bool2", bool2);
        builder.append("bool3", bool3);
        String actualResult = builder.complete().toString();
        String expectedResult = "[bool0=" + bool0 + ", bool1=" + bool1 + ", boolF=" + boolF + ", boolT=" + boolT + ", bool2=" + bool2 + ", bool3=" + bool3 +
                "]";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testBooleanFailBuilder() throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(BobjBooleanFail.class);
        builder.prepare();
        builder.append("bool0", false);
        builder.append("bool1", false);
        builder.append("boolF", false);
        builder.append("boolT", false);
        builder.append("bool2", false);
        builder.append("bool3", false);
        assertExceptionOnComplete(builder,
                "Не возможно определить соответствие между параметром в конструкторе и полем в объекта, если boolean.class в конструкторе больше 2х");
    }

    @Test
    public void testBobjInException() throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(BobjIn.class);
        builder.prepare();
        assertExceptionOnComplete(builder, "Поле 'inticBox' не содержится в списке значений. Класс 'class com.reforms.orm.reflex.BobjIn'");
        builder = new FullInstanceBuilder(BobjIn.class);
        builder.prepare();
        builder.append("inticBox", 1);
        assertExceptionOnComplete(builder, "Поле 'stringic' не содержится в списке значений. Класс 'class com.reforms.orm.reflex.BobjIn'");
        builder = new FullInstanceBuilder(BobjIn.class);
        builder.prepare();
        builder.append("inticBox", "wrong type");
        builder.append("stringic", 1);
        assertExceptionOnComplete(builder, "argument type mismatch");
    }

    @Test
    public void testBobjNoConstructorException() throws Exception {
        FullInstanceBuilder builder = new FullInstanceBuilder(BobjNoConstructor.class);
        builder.prepare();
        assertExceptionOnComplete(builder,
                "Не найдена информация о конструкторе, с помощью которого можно создать объект для класса 'class com.reforms.orm.reflex.BobjNoConstructor'");
    }

    private void assertExceptionOnComplete(FullInstanceBuilder builder, String errorText) {
        String actualErrorMessage = null;
        try {
            builder.complete();
            fail("Ожидается ошибка: " + errorText);
        } catch (Exception cause) {
            actualErrorMessage = cause.getMessage();
        }
        assertEquals(errorText, actualErrorMessage);
    }
}

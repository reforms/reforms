package com.reforms.orm.reflex;

import org.junit.Test;

import static com.reforms.orm.reflex.InstanceCreator.createInstanceCreator;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author evgenie
 */
public class UTestInstanceCreator {

    @Test
    public void testCreateOrm() {
        InstanceCreator creator = createInstanceCreator(Bobj.class);
        InstanceInfo instancesInfo = creator.getFirstInstanceInfo();
        String actualVaue = instancesInfo.getInstance1().toString();
        String expectedPattern = "^\\[flag=true\\, " +
                "flagBox=true\\, " +
                "bytic=0\\, " +
                "byticBox=0\\, " +
                "shortic=0\\, " +
                "shorticBox=0\\, " +
                "symbol=a\\, " +
                "symbolBox=a\\, " +
                "intic=0\\, " +
                "inticBox=0\\, " +
                "floatic=0.0\\, " +
                "floaticBox=0.0\\, " +
                "doublic=0.0\\, " +
                "doublicBox=0.0\\, " +
                "longic=0\\, " +
                "longicBox=0\\, " +
                "stringic=0\\, " +
                "decimalic=0.0\\, " +
                "datic=[^,]+\\, " +
                "timic=[^,]+\\, " +
                "timestampic=[^,]+\\, " +
                "enumic=SIMPLE\\, " +
                "bobjnnic=\\[" +
                "inticBox=0\\, " +
                "stringic=0\\]\\]$";
        assertTrue(actualVaue, actualVaue.matches(expectedPattern));
    }
}
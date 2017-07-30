package com.reforms.orm.reflex;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;

import org.junit.Test;

import static com.reforms.orm.reflex.EnumReflexor.createEnumReflexor;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum1.CE1_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum1.CE1_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum2.CE2_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum2.CE2_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum3.CE3_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum3.CE3_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum4.CE4_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum4.CE4_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum5.CE5_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum5.CE5_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum6.CE6_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum6.CE6_E2;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum7.CE7_E1;
import static com.reforms.orm.reflex.UTestEnumReflexor.ClassicEnum7.CE7_E2;
import static org.junit.Assert.assertEquals;

/**
 * Работа с enum через рефлексию
 * @author evgenie
 */
public class UTestEnumReflexor {

    @Test
    public void testEnumClassic1() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE1_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE1_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE1_E2));

        assertEquals(CE1_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE1_E2, enumReflexor.getEnumValue(2));
    }

    public static enum ClassicEnum1 {

        CE1_E1(1), CE1_E2(2);

        @TargetField
        private final int code;

        private ClassicEnum1(int code) {
            this.code = code;
        }

        @TargetMethod
        public static ClassicEnum1 code2Enum(int code) {
            return code == 1 ? CE1_E1 : code == 2 ? CE1_E2 : null;
        }

    }

    @Test
    public void testEnumClassic2() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE2_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE2_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE2_E2));

        assertEquals(CE2_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE2_E2, enumReflexor.getEnumValue(2));
    }

    public static enum ClassicEnum2 {

        CE2_E1(1), CE2_E2(2);

        private final int code;

        private ClassicEnum2(int code) {
            this.code = code;
        }

        @TargetMethod
        public int getCode() {
            return code;
        }

        @TargetMethod
        public static ClassicEnum2 code2Enum(int code) {
            return code == 1 ? CE2_E1 : code == 2 ? CE2_E2 : null;
        }

    }

    @Test
    public void testEnumClassic3() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE3_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE3_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE3_E2));

        assertEquals(CE3_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE3_E2, enumReflexor.getEnumValue(2));
    }

    // Nothin: be careful
    public static enum ClassicEnum3 {

        CE3_E1(1), CE3_E2(2);

        private final int code;

        private ClassicEnum3(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @Test
    public void testEnumClassic4() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE4_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE4_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE4_E2));

        assertEquals(CE4_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE4_E2, enumReflexor.getEnumValue(2));
    }

    // Nothin: be careful anonym
    public static enum ClassicEnum4 {

        CE4_E1(1){}, CE4_E2(2){};

        private final int code;

        private ClassicEnum4(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @Test
    public void testEnumClassic5() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE5_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE5_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE5_E2));

        assertEquals(CE5_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE5_E2, enumReflexor.getEnumValue(2));
    }

    // Nothin: be careful anonym 2 types is candidate to map (int and String) int - hight priority, because its first args in constructor
    public static enum ClassicEnum5 {

        CE5_E1(1, "one"), CE5_E2(2, "second");

        private final int code;
        private final String desc;
        private ClassicEnum5(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    @Test
    public void testEnumClassic6() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE6_E1.getClass());

        assertEquals("CE6_E1", enumReflexor.getAssignValue(CE6_E1));
        assertEquals("CE6_E2", enumReflexor.getAssignValue(CE6_E2));

        assertEquals(CE6_E1, enumReflexor.getEnumValue("CE6_E1"));
        assertEquals(CE6_E2, enumReflexor.getEnumValue("CE6_E2"));
    }

    // Nothin: be careful name based assosiating
    public static enum ClassicEnum6 {
        CE6_E1, CE6_E2
    }

    @Test
    public void testEnumClassic7() throws Exception {
        IEnumReflexor enumReflexor = createEnumReflexor(CE7_E1.getClass());

        assertEquals(1, enumReflexor.getAssignValue(CE7_E1));
        assertEquals(2, enumReflexor.getAssignValue(CE7_E2));

        assertEquals(CE7_E1, enumReflexor.getEnumValue(1));
        assertEquals(CE7_E2, enumReflexor.getEnumValue(2));
    }

    // Nothin: be careful anonym 2 types is candidate to map (int and String) int - hight priority, because its first args in constructor
    public static enum ClassicEnum7 {

        CE7_E1(1, "one"), CE7_E2(2, "second");

        @TargetField
        private final int code;

        private final String desc;

        private ClassicEnum7(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}

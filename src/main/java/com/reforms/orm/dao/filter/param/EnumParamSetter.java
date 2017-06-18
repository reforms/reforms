package com.reforms.orm.dao.filter.param;

import static com.reforms.orm.reflex.ClassUtils.isEnumClass;
import static com.reforms.orm.reflex.EnumReflexor.createEnumReflexor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.reflex.IEnumReflexor;

/**
 * Установить параметр типа Enum
 * @author evgenie
 */
@ThreadSafe
public class EnumParamSetter implements ParamSetter {

    private final ParamSetterFactory paramSetterFactory;

    public EnumParamSetter(ParamSetterFactory paramSetterFactory) {
        this.paramSetterFactory = paramSetterFactory;
    }

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        // Установка null. Проверить под разные СУБД
        if (value == null) {
            ps.setObject(index, null);
            return;
        }
        if (! acceptValue(value)) {
            throw new IllegalStateException("Невозможно установить значения для перечисления. Входящее значение: '" + value +
                    "', index = '" + index +"'");
        }
        Object assignValue = getAssignValue(value);
        ParamSetter paramSetter = paramSetterFactory.findParamSetter(assignValue);
        if (paramSetter == null) {
            throw new IllegalStateException("Невозможно найти ParamSetter для '" + value + "'");
        }
        paramSetter.setValue(assignValue, index, ps);
    }

    @Override
    public boolean acceptValue(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> clazz = value.getClass();
        if (isEnumClass(clazz)) {
            return true;
        }
        return false;
    }

    private Object getAssignValue(Object enumValue) {
        IEnumReflexor enumReflexor = createEnumReflexor(enumValue.getClass());
        return enumReflexor.getAssignValue(enumValue);
    }
}

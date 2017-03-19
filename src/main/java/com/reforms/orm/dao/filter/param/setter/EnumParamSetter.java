package com.reforms.orm.dao.filter.param.setter;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.reflex.IEnumReflexor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.reforms.orm.reflex.ClassUtils.isEnumClass;
import static com.reforms.orm.reflex.EnumReflexor.createEnumReflexor;

/**
 * Установить параметр типа Int
 * @author evgenie
 */
@ThreadSafe
public class EnumParamSetter implements ParamSetter {

    private ParamSetterFactory paramSetterFactory;

    public EnumParamSetter(ParamSetterFactory paramSetterFactory) {
        super();
        this.paramSetterFactory = paramSetterFactory;
    }

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
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

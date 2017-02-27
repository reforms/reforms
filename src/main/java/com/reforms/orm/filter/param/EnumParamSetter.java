package com.reforms.orm.filter.param;

import static com.reforms.orm.reflex.Reflexor.createReflexor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.TargetField;
import com.reforms.ann.TargetMethod;
import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.reflex.IFieldAcceptor;
import com.reforms.orm.reflex.IMethodAcceptor;
import com.reforms.orm.reflex.IReflexor;

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
        if (clazz.isEnum()) {
            return true;
        }
        if (clazz.isAnonymousClass() && clazz.getSuperclass().isEnum()) {
            return true;
        }
        return false;
    }

    private Object getAssignValue(Object value) {
        IReflexor reflexor = createReflexor(value.getClass());
        Object assignEnumValue = reflexor.getValue(new TargetAnnotationMethodAcceptor(value));
        if (assignEnumValue != null) {
            return assignEnumValue;
        }
        return reflexor.getValue(value, OrmConfigurator.get(TargetAnnotationFieldAcceptor.class));

    }

    private static class TargetAnnotationMethodAcceptor implements IMethodAcceptor {
        private Object enumValue;

        TargetAnnotationMethodAcceptor(Object enumValue) {
            this.enumValue = enumValue;
        }

        @Override
        public boolean acceptMethod(Method method) {
            return method.isAnnotationPresent(TargetMethod.class)
                    && (isGetter(method) || isNeedEnumValue(method));
        }

        @Override
        public Object getInstanceObjectFor(Method method) {
            return isGetter(method) ? enumValue : null;
        }

        @Override
        public Object[] getArgsFor(Method method) {
            return isNeedEnumValue(method) ? new Object[]{enumValue} : null;
        }

        private boolean isGetter(Method method) {
            return method.getParameterTypes().length == 0;
        }

        private boolean isNeedEnumValue(Method method) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1) {
                Class<?> paramClass = params[0];
                if (paramClass.isEnum()) {
                    return true;
                }
                if (paramClass.isAnonymousClass() && paramClass.getSuperclass().isEnum()) {
                    return true;
                }
            }
            return false;
        }
    }

    // TODO доработка:  сделать приват
    @ThreadSafe
    public static class TargetAnnotationFieldAcceptor implements IFieldAcceptor {
        @Override
        public boolean acceptField(Field field) {
            return field.isAnnotationPresent(TargetField.class);
        }
    }

}

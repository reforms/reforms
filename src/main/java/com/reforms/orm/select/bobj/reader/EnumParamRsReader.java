package com.reforms.orm.select.bobj.reader;

import static com.reforms.orm.reflex.Reflexor.createReflexor;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import com.reforms.ann.TargetMethod;
import com.reforms.orm.reflex.IMethodAcceptor;
import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Enum из выборки ResultSet
 * @author evgenie
 */
class EnumParamRsReader implements IParamRsReader<Enum<?>> {

    private ParamRsReaderFactory factory;

    public EnumParamRsReader(ParamRsReaderFactory factory) {
        this.factory = factory;
    }

    @Override
    public Enum<?> readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws Exception {
        TargetAnnotationMethodAcceptor methodAcceptor = new TargetAnnotationMethodAcceptor();
        IReflexor reflexor = createReflexor(toBeClass);
        Method method = reflexor.findMethod(methodAcceptor);
        if (method == null) {
            throw new IllegalStateException("Невозможно прочитать значения для перечисления. Класс перечисления: '" + toBeClass +
                    "', columnName = '" + column.getColumnName() +"'. Необходмио добавить аннотацию TargetMethod");
        }
        Class<?> assignToBeReadClass = method.getParameterTypes()[0];
        IParamRsReader<?> realReader = factory.getParamRsReader(assignToBeReadClass);
        if (realReader == null) {
            throw new IllegalStateException("Не найден IParamRsReader для чтения из ResultSet значения для класса '" + assignToBeReadClass + "'");
        }
        Object assignValue = realReader.readValue(column, rs, assignToBeReadClass);
        methodAcceptor.setAssignValue(assignValue);
        return (Enum<?>) reflexor.getValue(methodAcceptor);
    }

    private static class TargetAnnotationMethodAcceptor implements IMethodAcceptor {
        private Object assignValue;

        public void setAssignValue(Object assignValue) {
            this.assignValue = assignValue;
        }

        @Override
        public boolean acceptMethod(Method method) {
            return method.isAnnotationPresent(TargetMethod.class) && isAssignValue(method);
        }

        @Override
        public Object getInstanceObjectFor(Method method) {
            return null;
        }

        @Override
        public Object[] getArgsFor(Method method) {
            return new Object[]{assignValue};
        }

        private boolean isAssignValue(Method method) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                return false;
            }
            Class<?> returnClass = method.getReturnType();
            if (returnClass.isEnum()) {
                return true;
            }
            if (returnClass.isAnonymousClass() && returnClass.getSuperclass().isEnum()) {
                return true;
            }
            return false;
        }
    }

}

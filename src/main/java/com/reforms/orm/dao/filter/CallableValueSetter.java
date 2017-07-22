package com.reforms.orm.dao.filter;

import com.reforms.orm.dao.filter.param.ParamSetterFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Устанавливает значения с заданного индекса в CallableStatement
 * @author evgenie
 */
public class CallableValueSetter extends PsValuesSetter {

    private final boolean typed;
    public CallableValueSetter(boolean typed, ParamSetterFactory paramSetterFactory) {
        super(paramSetterFactory);
        this.typed = typed;
    }

    public int setParamsAndReturnType(Integer returnType, CallableStatement cs) throws SQLException {
        if (typed) {
            if (returnType == null) {
                throw new IllegalStateException("Необходимо указать sql тип из java.sql.Types в аннотации @TargetQuery(returnType = ???)");
            }
            cs.registerOutParameter(index++, returnType);
        }
        return setParamsTo(cs);
    }
}

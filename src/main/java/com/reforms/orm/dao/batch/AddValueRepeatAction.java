package com.reforms.orm.dao.batch;

import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.filter.IPsValuesSetter;

/**
 * Действие по добавлению в PS значения
 * @author evgenie
 */
class AddValueRepeatAction {

    private final String prefix;
    private final IGetAction getValueAction;

    AddValueRepeatAction(String prefix, IGetAction getValueAction) {
        this.prefix = prefix;
        this.getValueAction = getValueAction;
    }

    void addValue(IPriorityValues values, IPsValuesSetter setter) {
        Object value = getValueAction.getPriorityValue(values);
        setter.addFilterValue(prefix, value);
    }

}

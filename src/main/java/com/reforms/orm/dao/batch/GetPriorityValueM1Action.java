package com.reforms.orm.dao.batch;

import com.reforms.orm.dao.IPriorityValues;

class GetPriorityValueM1Action implements IGetAction {

    private final int priority;
    private final String key;

    GetPriorityValueM1Action(int priority, String key) {
        this.priority = priority;
        this.key = key;
    }

    @Override
    public Object getPriorityValue(IPriorityValues values) {
        return values.getPriorityValue(priority, key);
    }

}

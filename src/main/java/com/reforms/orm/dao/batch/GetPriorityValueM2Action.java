package com.reforms.orm.dao.batch;

import com.reforms.orm.dao.IPriorityValues;

class GetPriorityValueM2Action implements IGetAction {

    private final int priority;
    private final int key;

    GetPriorityValueM2Action(int priority, int key) {
        this.priority = priority;
        this.key = key;
    }

    @Override
    public Object getPriorityValue(IPriorityValues values) {
        return values.getPriorityValue(priority, key);
    }

}

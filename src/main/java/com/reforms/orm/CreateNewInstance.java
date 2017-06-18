package com.reforms.orm;

import com.reforms.ann.TargetApi;

@FunctionalInterface
@TargetApi
public interface CreateNewInstance<Instance> {

    public Instance createNew(Instance current);
}

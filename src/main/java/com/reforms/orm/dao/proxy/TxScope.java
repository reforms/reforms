package com.reforms.orm.dao.proxy;

import com.reforms.ann.TargetApi;

@TargetApi
@FunctionalInterface
public interface TxScope {

    void apply();
}
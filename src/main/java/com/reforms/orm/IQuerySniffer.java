package com.reforms.orm;

import com.reforms.ann.TargetApi;

@FunctionalInterface
@TargetApi
public interface IQuerySniffer {

    void onQuery(String beforeModifingQuery, String afterModifingQuery);
}

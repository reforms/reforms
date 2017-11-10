package com.reforms.orm;

import com.reforms.ann.TargetApi;

@FunctionalInterface
@TargetApi
public interface IQuerySniffer {

    String onQuery(String beforeModifingQuery, String afterModifingQuery);
}

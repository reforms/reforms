package com.reforms.orm;

import com.reforms.ann.ThreadSafe;

/**
 *
 * @author palihov
 */
@ThreadSafe
class QuerySniffer implements IQuerySniffer {

    @Override
    public String onQuery(String beforeModifingQuery, String afterModifingQuery) {
        return afterModifingQuery;
    }
}
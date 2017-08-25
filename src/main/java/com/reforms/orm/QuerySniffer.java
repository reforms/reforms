package com.reforms.orm;

import com.reforms.ann.ThreadSafe;

/**
 *
 * @author palihov
 */
@ThreadSafe
class QuerySniffer implements IQuerySniffer {
    @Override
    public void onQuery(String beforeModifingQuery, String afterModifingQuery) {
    }
}
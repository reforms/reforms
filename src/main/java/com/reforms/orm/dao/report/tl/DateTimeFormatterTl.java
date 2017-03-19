package com.reforms.orm.dao.report.tl;

import java.text.SimpleDateFormat;

public class DateTimeFormatterTl extends ThreadLocal<SimpleDateFormat> {

    private String pattern;

    public DateTimeFormatterTl(String pattern) {
        this.pattern = pattern;
    }

    @Override
    protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(pattern);
    }

}

package com.reforms.orm.select.report.tl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberFormatterTl extends ThreadLocal<DecimalFormat> {

    private String pattern;

    public NumberFormatterTl(String pattern) {
        this.pattern = pattern;
    }

    @Override
    protected DecimalFormat initialValue() {
        String[] patterns = parseFormatter();
        if (patterns.length == 1) {
            return new DecimalFormat(pattern);
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        String groupInfo = patterns[1];
        if (!"x".equals(groupInfo)) {
            symbols.setGroupingSeparator(groupInfo.charAt(0));
        }
        String separatorInfo = patterns[2];
        if (!"x".equals(separatorInfo)) {
            symbols.setDecimalSeparator(separatorInfo.charAt(0));
        }
        return new DecimalFormat(patterns[0], symbols);
    }

    private String[] parseFormatter() {
        int vIndex = pattern.lastIndexOf('|');
        if (vIndex == -1) {
            return new String[] { pattern };
        }
        String realPattern = pattern.substring(0, vIndex).trim();
        String extPart = pattern.substring(vIndex + 1);
        String[] parts = new String[] { realPattern, "x", "x" };
        if (extPart.length() > 0) {
            parts[1] = "" + extPart.charAt(0);
        }
        if (extPart.length() > 1) {
            parts[2] = "" + extPart.charAt(1);
        }
        return parts;
    }

}

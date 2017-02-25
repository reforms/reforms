package com.reforms.orm;

/**
 * TODO add xml config for ReportConfig
 * @author evgenie
 */
public class ConverterConfig {
    
    public ConverterConfig() {
    }

    private String datePattern = "dd.MM.yyyy";

    private String timePattern = "HH:mm:ss.SSS";

    private String timestampPattern = datePattern + " " + timePattern;

    /** Первая часть '#.00' - обычный шаблон для DecimalFormat,
     * вторая часть - настройка для DecimalFormatSymbols, где
     *    - 1ый символ setGroupingSeparator,
     *    - 2ой символ setDecimalSeparator
     *    если символ изменять не требуется, можно использовать символ x
     * По умолчанию выбран шаблон с разделением групп пробелом и дробной частью - точкой
     */
    private String numberPattern = "#.00 | .";

    private String asciiEncoding = "UTF8";

    private String binaryEncoding = "UTF8";

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public void setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
    }

    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(String numberPattern) {
        this.numberPattern = numberPattern;
    }

    public String getAsciiEncoding() {
        return asciiEncoding;
    }

    public void setAsciiEncoding(String asciiEncoding) {
        this.asciiEncoding = asciiEncoding;
    }

    public String getBinaryEncoding() {
        return binaryEncoding;
    }

    public void setBinaryEncoding(String binaryEncoding) {
        this.binaryEncoding = binaryEncoding;
    }

}

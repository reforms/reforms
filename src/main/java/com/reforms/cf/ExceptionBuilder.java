package com.reforms.cf;

import java.text.MessageFormat;

/**
 * Help for exception building.
 * Ошибки приложения jEye Цель: максимально
 * детализировать причину и следствие, указать область возникновения ошибки
 *
 * @author evgenie
 */
public class ExceptionBuilder {

    private String area;
    private String reason;
    private String effect;
    private String action;
    private Object details;
    private Throwable cause;

    public ExceptionBuilder area(String area) {
        return area(area, new Object[]{});
    }

    public ExceptionBuilder area(String area, Object ... meta) {
        this.area = format(area, meta);
        return this;
    }

    public ExceptionBuilder reason(String reason) {
        return reason(reason, new Object[]{});
    }

    public ExceptionBuilder reason(String reason, Object ... meta) {
        this.reason = format(reason, meta);
        return this;
    }

    public ExceptionBuilder effect(String effect) {
        return effect(effect, new Object[]{});
    }

    public ExceptionBuilder effect(String effect, Object ... meta) {
        this.effect = format(effect, meta);
        return this;
    }

    public ExceptionBuilder action(String action) {
        return action(action, new Object[]{});
    }

    public ExceptionBuilder action(String action, Object ... meta) {
        this.action = format(action, meta);
        return this;
    }

    public ExceptionBuilder details(Object details, Object ... meta) {
        this.details = format(details.toString(), meta);
        return this;
    }

    private String format(String data, Object ... meta) {
        if (data == null) {
            return endln("");
        }
        if (meta != null && meta.length > 0) {
            return endln(MessageFormat.format(data, meta));
        }
        return endln(data);
    }

    private String endln(String data) {
        return data + "\n";
    }

    public ExceptionBuilder cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ClassFormatException exception() {
        StringBuilder builder = new StringBuilder();
        builder.append("Ошибка:\n");
        builder.append("Область: ").append(area);
        builder.append("Причина: ").append(reason);
        builder.append("Следствие: ").append(effect);
        builder.append("Действие: ").append(action);
        if (details != null) {
            builder.append("Детали: ").append(details);
        }
        String message = builder.toString();
        return cause == null ? new ClassFormatException(message) : new ClassFormatException(message, cause);
    }
}

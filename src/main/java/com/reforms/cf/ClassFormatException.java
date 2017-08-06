package com.reforms.cf;

/**
 * Ошибки приложения jEye Цель: максимально детализировать причину и следствие,
 * указать область возникновения ошибки
 *
 * @author evgenie
 *
 */
public class ClassFormatException extends RuntimeException {

    public static final String PARSE_FILE_AREA = "Разбор class файла";
    public static final String PARSE_DESCRIPTOR_AREA = "Разбор дескриптора";
    public static final String PARSE_SIGNATURE_AREA = "Разбор сигнатур";
    public static final String CONSTANT_POOL_AREA = "ConstantPool";

    public ClassFormatException(String message) {
        super(message);
    }

    public ClassFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}

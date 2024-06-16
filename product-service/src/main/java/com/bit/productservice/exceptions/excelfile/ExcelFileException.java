package com.bit.productservice.exceptions.excelfile;

/**
 * Exception thrown when there is a problem generating exel file.
 */
public class ExcelFileException extends RuntimeException {
    /**
     * Constructs a new ExcelFileException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ExcelFileException(String message) {
        super(message);
    }
}

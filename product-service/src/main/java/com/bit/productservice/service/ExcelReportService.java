package com.bit.productservice.service;

import java.io.ByteArrayInputStream;

/**
 * Service interface for exporting data to Excel.
 */
public interface ExcelReportService {
    /**
     * Export product data to Excel.
     *
     * @return A {@link ByteArrayInputStream} containing the exported Excel data.
     * @throws RuntimeException If there is an error during the export process.
     */
    ByteArrayInputStream exportProductDataToExcel();
}
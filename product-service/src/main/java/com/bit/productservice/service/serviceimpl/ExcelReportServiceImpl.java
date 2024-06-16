package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.excelfile.ExcelFileException;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.ExcelReportService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportServiceImpl implements ExcelReportService {
    private final ProductRepository productRepository;
    private static final Logger logger = LogManager.getLogger(ExcelReportServiceImpl.class);

    @Override
    public ByteArrayInputStream exportProductDataToExcel() {
        logger.trace("Exporting product data to Excel...");

        List<Product> products = productRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Create a new sheet named "Products"
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            String[] headers = {"ID", "Name", "Price", "Stock Amount", "Category", "Creation Date", "In Stock"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                // Create a cell for each header and set its value and style
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // Create data rows
            int rowIdx = 1;
            for (Product product : products) {
                // Create a new row for each product and set its cell values
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getStockAmount());
                row.createCell(4).setCellValue(product.getCategory());
                row.createCell(5).setCellValue(product.getCreationDate().toString());
                row.createCell(6).setCellValue(product.isInStock() ? "Yes" : "No");
            }

            workbook.write(out);

            logger.trace("Exported product data to excel file");

            // Return a ByteArrayInputStream containing the Excel file data
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ExcelFileException("Failed to generate Excel file: " + e.getMessage());
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerCellStyle.setFont(font);
        return headerCellStyle;
    }
}
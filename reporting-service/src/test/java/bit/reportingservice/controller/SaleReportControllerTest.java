package bit.reportingservice.controller;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaleReportControllerTest {
    @Mock
    ReportingService reportingService;

    @InjectMocks
    SaleReportController saleReportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listReports_shouldReturnListOfReports() {
        List<ListReportsReq> reports = Collections.singletonList(new ListReportsReq());
        when(reportingService.listReports(anyInt(), anyInt(), any(), any(), any())).thenReturn(reports);

        List<ListReportsReq> response = saleReportController.listReports(0, 10, "COMPLETED_DATE_DESC", null, null);

        assertEquals(reports, response);
        verify(reportingService).listReports(0, 10, "COMPLETED_DATE_DESC", null, null);
    }

    @Test
    void generatePdfReceipt_shouldReturnPdfBytes() throws IOException {
        byte[] pdfBytes = new byte[10];
        when(reportingService.generatePdfReceipt(1L)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> response = saleReportController.generatePdfReceipt(1L);

        assertEquals(pdfBytes.length, Objects.requireNonNull(response.getBody()).length);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/pdf", Objects.requireNonNull(response.getHeaders().getContentType()).toString());
        assertEquals("sale1.pdf", response.getHeaders().getContentDisposition().getFilename());
        verify(reportingService).generatePdfReceipt(1L);
    }
}

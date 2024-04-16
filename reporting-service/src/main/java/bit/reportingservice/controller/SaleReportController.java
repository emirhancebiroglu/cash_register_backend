package bit.reportingservice.controller;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class SaleReportController {
    private final ReportingService reportingService;
    @GetMapping("/list-reports")
    public List<ListReportsReq> listReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "COMPLETED_DATE_DESC") String sortBy,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String paymentMethod
    ){
        return reportingService.listReports(page, size, sortBy, filterBy, paymentMethod);
    }

    @GetMapping("/generate-receipt/{reportId}")
    public ResponseEntity<byte[]> generatePdfReceipt(@PathVariable Long reportId) throws IOException {
        byte[] pdfBytes = reportingService.generatePdfReceipt(reportId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "sale" + reportId + ".pdf");
        headers.setContentLength(pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
package bit.reportingservice.controller;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.entity.FilterBy;
import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.SortBy;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "COMPLETED_DATE_DESC") SortBy sortBy,
            @RequestParam(required = false) FilterBy filterBy,
            @RequestParam(required = false) PaymentMethod paymentMethod
    ){
        return reportingService.listReports(page, size, sortBy, filterBy, paymentMethod);
    }
}
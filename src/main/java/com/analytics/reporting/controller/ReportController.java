package com.analytics.reporting.controller;


import com.analytics.reporting.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ReportController {


    private final ReportService reportService;

    @GetMapping("/report/{appId}")
    public ResponseEntity<?> getAnalyticsReport(@PathVariable String appId, HttpServletRequest httpServletRequest)
    {
        try {
            String actualAppId = httpServletRequest.getAttribute("appId").toString();
            if (!actualAppId.equalsIgnoreCase(appId)) {
                return ResponseEntity.badRequest().body(" Cannot access report of other app ");
            }
            Object res = reportService.getReport(appId);
            return ResponseEntity.ok(res);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Eception occured "+e);
        }

    }
}

package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.services.statistic.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCustomers", statisticsService.countCustomers());
        statistics.put("totalCourses", statisticsService.countCourses());
        statistics.put("totalRevenue", statisticsService.calculateTotalRevenue());

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/revenue-by-month")
    public ResponseEntity<Map<String, Double>> getRevenueByMonth() {
        Map<String, Double> revenueByMonth = statisticsService.calculateRevenueByMonth();
        return ResponseEntity.ok(revenueByMonth);
    }
}

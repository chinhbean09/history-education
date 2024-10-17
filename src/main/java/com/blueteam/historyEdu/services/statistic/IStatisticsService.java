package com.blueteam.historyEdu.services.statistic;

import java.util.Map;

public interface IStatisticsService {

    Long countCustomers();

    Long countCourses();

    Double calculateTotalRevenue();

    Map<String, Double> calculateRevenueByMonth();
}

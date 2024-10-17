package com.blueteam.historyEdu.services.statistic;

import com.blueteam.historyEdu.repositories.ICourseRepository;
import com.blueteam.historyEdu.repositories.IPurchaseRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService implements IStatisticsService {

    private final IUserRepository userRepository;
    private final IPurchaseRepository purchaseRepository;
    private final ICourseRepository courseRepository;

    @Override
    public Long countCustomers() {
        return userRepository.countByRole_RoleName("CUSTOMER");
    }

    @Override
    public Long countCourses() {
        return courseRepository.count();
    }

    @Override
    public Double calculateTotalRevenue() {
        return purchaseRepository.sumTotalRevenue();
    }

    @Override
    public Map<String, Double> calculateRevenueByMonth() {
        Map<String, Double> revenueByMonth = new HashMap<>();

        // Duyệt qua từng tháng từ 1 đến 12 và tính tổng doanh thu
        for (int month = 1; month <= 12; month++) {
            Double totalRevenue = purchaseRepository.sumRevenueByMonth(month);
            revenueByMonth.put("Month " + month, totalRevenue != null ? totalRevenue : 0.0);
        }

        return revenueByMonth;
    }
}

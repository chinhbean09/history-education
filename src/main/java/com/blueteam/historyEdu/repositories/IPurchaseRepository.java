package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.entities.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.price) FROM Purchase p WHERE p.packageStatus = 'PAID'")
    Double sumTotalRevenue();

    @Query("SELECT SUM(p.price) FROM Purchase p WHERE MONTH(p.purchaseDate) = :month AND p.packageStatus = 'PAID'")
    Double sumRevenueByMonth(int month);
}

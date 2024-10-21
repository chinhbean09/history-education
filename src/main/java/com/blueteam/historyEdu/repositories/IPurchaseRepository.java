package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.price) FROM Purchase p WHERE p.packageStatus = 'PAID'")
    Double sumTotalRevenue();

    @Query("SELECT SUM(p.price) FROM Purchase p WHERE MONTH(p.purchaseDate) = :month AND p.packageStatus = 'PAID'")
    Double sumRevenueByMonth(int month);

    List<Purchase> findAllByPackageStatusAndExpiryDateBefore(PackageStatus status, LocalDateTime expiryDate);

}

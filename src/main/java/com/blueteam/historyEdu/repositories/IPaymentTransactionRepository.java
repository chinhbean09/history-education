package com.blueteam.historyEdu.repositories;

import com.chinhbean.bookinghotel.entities.PaymentTransaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface IPaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM PaymentTransaction pt WHERE pt.emailGuest = :emailGuest")
    void deleteByEmailGuest(String emailGuest);

    @Query(value = "SELECT * FROM payment_transactions pt WHERE pt.email_guest = :emailGuest ORDER BY pt.create_date DESC LIMIT 1", nativeQuery = true)
    Optional<PaymentTransaction> findByEmailGuest(String emailGuest);

    @Query("SELECT pt.servicePackage.id, SUM(pt.servicePackage.price) FROM PaymentTransaction pt WHERE pt.transactionCode IS NOT NULL GROUP BY pt.servicePackage.id")
    List<Object[]> findTotalRevenueByPackage();

}


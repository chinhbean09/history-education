package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.entities.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {

}

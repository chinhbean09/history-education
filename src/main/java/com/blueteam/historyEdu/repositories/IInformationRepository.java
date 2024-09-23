package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Information;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInformationRepository extends JpaRepository<Information, Long> {
}

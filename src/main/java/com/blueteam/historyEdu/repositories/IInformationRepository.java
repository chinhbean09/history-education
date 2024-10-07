package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Information;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IInformationRepository extends JpaRepository<Information, Long> {

    List<Information> findAllByLessonIdOrderBySttAsc(Long lessonId);
}

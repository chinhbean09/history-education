package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.InfoProgress;
import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.QuizProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoProgressRepository extends JpaRepository<InfoProgress, Long> {
    InfoProgress findByProgressAndInfoId(Progress progress, Long infoId);
}

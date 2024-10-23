package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.InfoProgress;
import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.QuizProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InfoProgressRepository extends JpaRepository<InfoProgress, Long> {
    InfoProgress findByProgressAndInfoId(Progress progress, Long infoId);

    @Transactional
    @Modifying
    @Query("DELETE FROM InfoProgress vp WHERE vp.information.id = :infoId")
    void deleteByInfoId(Long infoId);
}

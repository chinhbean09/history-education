package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.QuizProgress;
import com.blueteam.historyEdu.entities.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface QuizProgressRepository extends JpaRepository<QuizProgress, Long> {
    QuizProgress findByProgressAndQuizId(Progress progress, long quizId);

    @Transactional
    @Modifying
    @Query("DELETE FROM QuizProgress vp WHERE vp.quiz.id = :quizId")
    void deleteByQuizId(long quizId);
}

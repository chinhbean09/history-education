package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.QuizProgress;
import com.blueteam.historyEdu.entities.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizProgressRepository extends JpaRepository<QuizProgress, Long> {
    QuizProgress findByProgressAndQuizId(Progress progress, String quizId);
}

package com.blueteam.historyEdu.repositories;
import com.blueteam.historyEdu.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long quizId); // Method to find questions by quiz ID


}

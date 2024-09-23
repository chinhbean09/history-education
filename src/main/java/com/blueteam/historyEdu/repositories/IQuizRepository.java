package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q JOIN FETCH q.questions qu JOIN FETCH qu.answers WHERE q.id = :id")
    Optional<Quiz> findQuizByIdWithQuestionsAndAnswers(@Param("id") Long id);


}

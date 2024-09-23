package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, Long> {


}

package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILessonRepository extends JpaRepository<Lesson, Long> {
}

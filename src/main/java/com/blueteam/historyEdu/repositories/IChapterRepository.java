package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findAllByCourseId(Long courseId);
}

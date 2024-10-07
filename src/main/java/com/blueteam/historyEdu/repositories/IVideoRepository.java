package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByLessonIdOrderBySttAsc(Long lessonId);
}

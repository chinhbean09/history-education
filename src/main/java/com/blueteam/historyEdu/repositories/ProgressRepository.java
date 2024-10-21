package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Progress;
import com.blueteam.historyEdu.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    //  tiến trình của một người dùng trong một khóa học
    List<Progress> findByUserIdAndCourseId(Long userId, Long courseId);

    //  tiến trình của một người dùng trong một chương học cụ thể
    //Progress findByUserIdAndChapterId(Long userId, Long chapterId);

    List<Progress> findByUserIdAndChapterId(Long userId, Long chapterId);

    Optional<Progress> findFirstByUserIdAndChapterId(Long userId, Long chapterId);

    Optional<Progress> findByUserAndCourse(User user, Course course);

    List<Progress> findByCourse(Course course);

    boolean existsByUserAndCourse(User user, Course course);

    boolean existsByUserAndChapterId(User user, Long chapterId);

}

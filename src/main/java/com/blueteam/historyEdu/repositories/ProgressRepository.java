package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    //  tiến trình của một người dùng trong một khóa học
    List<Progress> findByUserIdAndCourseId(Long userId, Long courseId);

    //  tiến trình của một người dùng trong một chương học cụ thể
    Progress findByUserIdAndChapterId(Long userId, Long chapterId);
}

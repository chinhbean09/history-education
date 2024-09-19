package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICourseRepository extends JpaRepository<Course, Long> {
    Course findByCourseName(String courseName);
}

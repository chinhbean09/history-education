package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {
    Course findByCourseName(String courseName);

    List<Course> findAllByPriceGreaterThan(int i);

    List<Course> findAllByPriceEquals(int i);

    Optional<Course> findById(Long id);
}

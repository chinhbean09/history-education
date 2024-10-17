package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {
    Course findByCourseName(String courseName);

    List<Course> findAllByPriceGreaterThan(int i);

    List<Course> findAllByPriceEquals(int i);

    Optional<Course> findById(Long id);

    // query that help search course by name
    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %?1%")
    List<Course> findAllByCourseNameContaining(String courseName);
}

package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
//    @Query("SELECT r FROM Review r JOIN FETCH r.course WHERE r.course.id = :courseId")
    // using native query
    @Query(value = "SELECT * FROM reviews WHERE course_id = :courseId", nativeQuery = true)
    List<Review> findAllByCourseId(@Param("courseId") Long courseId);
}

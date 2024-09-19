package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReviewRepository extends JpaRepository<Review, Long> {
}

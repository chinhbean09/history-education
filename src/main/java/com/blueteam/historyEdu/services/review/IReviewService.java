package com.blueteam.historyEdu.services.review;

import com.blueteam.historyEdu.dtos.ReviewDTO;
import com.blueteam.historyEdu.entities.Review;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ReviewListResponse;

import java.util.List;

public interface IReviewService {

    CourseResponse addReview(Long courseId, ReviewDTO reviewDTO) throws DataNotFoundException, PermissionDenyException;

    void deleteReview(Long reviewId) throws DataNotFoundException;

    Review addReviewToCourse(Long courseId, Review review) throws DataNotFoundException;

    // get all review by course id
    List<ReviewListResponse> getAllReviewByCourseId(Long courseId) throws DataNotFoundException;

    String createReview(Long courseId, ReviewDTO reviewDTO);
}

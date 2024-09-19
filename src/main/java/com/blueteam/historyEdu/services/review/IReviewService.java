package com.blueteam.historyEdu.services.review;

import com.blueteam.historyEdu.dtos.ReviewDTO;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.CourseResponse;

public interface IReviewService {

    CourseResponse addReview(Long courseId, ReviewDTO reviewDTO) throws DataNotFoundException, PermissionDenyException;

    void deleteReview(Long reviewId) throws DataNotFoundException;
}

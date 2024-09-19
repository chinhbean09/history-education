package com.blueteam.historyEdu.services.review;

import com.blueteam.historyEdu.dtos.ReviewDTO;
import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Review;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.ICourseRepository;
import com.blueteam.historyEdu.repositories.IReviewRepository;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final IReviewRepository reviewRepository;
    private final ICourseRepository courseRepository;

    @Override
    @Transactional
    public CourseResponse addReview(Long courseId, ReviewDTO reviewDTO) throws DataNotFoundException, PermissionDenyException {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the current user is a CUSTOMER
        if (currentUser.getRole().getRoleName().equals("CUSTOMER")) {
            // Retrieve the course by ID
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new DataNotFoundException(MessageKeys.COURSE_NOT_FOUND));

            // Create a new review and associate the current user and course
            Review review = reviewDTO.toEntity(course);
            review.setCourse(course);
            review.setUser(currentUser);  // Set the current user to the review

            // Save the review
            reviewRepository.save(review);

            // Add the review to the course's reviews
            course.getReviews().add(review);

            // Return the course response with reviews
            return CourseResponse.fromCourse(course);
        } else {
            throw new PermissionDenyException(MessageKeys.PERMISSION_DENIED);
        }
    }


    @Override
    @Transactional
    public void deleteReview(Long reviewId) throws DataNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }
}

package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.ReviewDTO;
import com.blueteam.historyEdu.exceptions.DataNotFoundException;
import com.blueteam.historyEdu.responses.CourseResponse;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.review.IReviewService;
import com.blueteam.historyEdu.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    // api create review
//    @PostMapping("/create/{courseId}")
//    public ResponseEntity<ResponseObject> createReview(@PathVariable Long courseId, @RequestBody ReviewDTO reviewDTO) {
//        try {
//            CourseResponse courseResponse = reviewService.addReview(courseId, reviewDTO);
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    ResponseObject.builder()
//                            .data(courseResponse)
//                            .message(MessageKeys.REVIEW_CREATED_SUCCESSFULLY)
//                            .status(HttpStatus.OK)
//                            .build()
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    ResponseObject.builder()
//                            .data(null)
//                            .message(e.getMessage())
//                            .status(HttpStatus.BAD_REQUEST)
//                            .build()
//            );
//        }
//    }
    @PostMapping("/create/{courseId}")
    public ResponseEntity<String> createReview(@PathVariable Long courseId, @RequestBody ReviewDTO reviewDTO) {
        try {
            String message = reviewService.createReview(courseId, reviewDTO);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // api delete review
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ResponseObject> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(MessageKeys.REVIEW_DELETED_SUCCESSFULLY)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    // api get all review by course id
    @GetMapping("/get-all-reviews-by-course/{courseId}")
    public ResponseEntity<ResponseObject> getAllReviewsByCourseId(@PathVariable Long courseId) throws DataNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .data(reviewService.getAllReviewByCourseId(courseId))
                        .message(MessageKeys.REVIEWS_FETCHED_SUCCESSFULLY)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}

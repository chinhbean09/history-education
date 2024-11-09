package com.blueteam.historyEdu.responses;


import com.blueteam.historyEdu.entities.Review;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewListResponse {

    private Long courseId;
    private String userName;
    private String comment;
    private Double rating;


    public static ReviewListResponse fromReview(Review review) {
        return ReviewListResponse.builder()
                .courseId(review.getCourse().getId())
                .userName(review.getUser().getFullName())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }
}

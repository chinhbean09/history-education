package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Review;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private String user;
    private String comment;
    private Double rating;


    public static ReviewResponse fromReview(Review review) {
        return ReviewResponse.builder()
                .user(review.getUser().getFullName())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }
}

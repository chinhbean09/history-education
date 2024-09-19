package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Course;
import com.blueteam.historyEdu.entities.Review;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewDTO {

    private String comment;
    private Double rating;


    public Review toEntity(Course course) {
        return Review.builder()
                .course(course)
                .comment(comment)
                .rating(rating)
                .build();
    }
}

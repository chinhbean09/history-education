package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Course;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllCourseResponse {

    private Long id;
    @JsonProperty("courseName")
    private String courseName;
    @JsonProperty("image")
    private String image;
    @JsonProperty("price")
    private Long price;
    @JsonProperty("totalDuration")
    private Long totalDuration;
    @JsonProperty("totalLessons")
    private Long totalLessons;

    public static GetAllCourseResponse fromCourse(Course course) {
        return GetAllCourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .image(course.getImage())
                .price(course.getPrice())
                .totalDuration(course.getTotalDuration())
                .totalLessons(course.getTotalLessons())
                .build();
    }
}

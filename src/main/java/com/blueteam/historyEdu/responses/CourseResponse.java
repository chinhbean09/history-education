package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Course;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    @JsonProperty("courseName")
    private String courseName;
    @JsonProperty("introductionVideo")
    private String introductionVideoUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("moreInformation")
    private String moreInformation;
    @JsonProperty("image")
    private String image;
    @JsonProperty("totalDuration")
    private Long totalDuration;
    @JsonProperty("totalChapter")
    private Long totalChapter;
    @JsonProperty("totalLessons")
    private Long totalLessons;
//    @JsonProperty("price")
//    private Long price;
    @JsonProperty("rating")
    private Long rating;
    @JsonProperty("whatYouWillLearn")
    private List<String> whatsLearned;
    @JsonProperty("requireToPass")
    private List<String> requireToPass;
    @JsonProperty("reviews")
    private List<ReviewResponse> reviews;
    @JsonProperty("chapters")
    private List<ChapterResponse> chapters;

    private boolean isEnroll;

    public static CourseResponse fromCourse(Course course) {

        // Handle potential null reviews or chapters
        List<ReviewResponse> reviews = (course.getReviews() != null) ?
                course.getReviews().stream().map(ReviewResponse::fromReview).toList() :
                new ArrayList<>();

        List<ChapterResponse> chapters = (course.getChapters() != null) ?
                course.getChapters().stream().map(ChapterResponse::fromChapter).toList() :
                new ArrayList<>();

        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .introductionVideoUrl(course.getIntroductionVideoUrl())
                .description(course.getDescription())
                .moreInformation(course.getMoreInformation())
                .image(course.getImage())
                .chapters(chapters)
                .totalDuration(course.getTotalDuration())
                .totalChapter(course.getTotalChapter())
                .totalLessons(course.getTotalLessons())
//                .price(course.getPrice())
                .rating(course.getRating())
                .whatsLearned(course.getWhatsLearned())
                .requireToPass(course.getRequireToPass())
                .reviews(reviews)
                .build();
    }

    public static CourseResponse fromCourseDetail(Course course, boolean isEnroll) {

        // Handle potential null reviews or chapters
        List<ReviewResponse> reviews = (course.getReviews() != null) ?
                course.getReviews().stream().map(ReviewResponse::fromReview).toList() :
                new ArrayList<>();

        List<ChapterResponse> chapters = (course.getChapters() != null) ?
                course.getChapters().stream().map(ChapterResponse::fromChapter).toList() :
                new ArrayList<>();

        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .introductionVideoUrl(course.getIntroductionVideoUrl())
                .description(course.getDescription())
                .moreInformation(course.getMoreInformation())
                .image(course.getImage())
                .chapters(chapters)
                .totalDuration(course.getTotalDuration())
                .totalChapter(course.getTotalChapter())
                .totalLessons(course.getTotalLessons())
                .isEnroll(isEnroll)
//                .price(course.getPrice())
                .rating(course.getRating())
                .whatsLearned(course.getWhatsLearned())
                .requireToPass(course.getRequireToPass())
                .reviews(reviews)
                .build();
    }


}

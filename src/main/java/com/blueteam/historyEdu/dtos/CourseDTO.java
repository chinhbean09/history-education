package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Course;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseDTO {

    private String courseName;
    private String moreInformation;
    private String description;
    private String image;
    private String introductionVideoUrl;
    private Long totalDuration;
    private Long totalChapter;
    private Long totalLessons;
    private Long price;
    private Long rating;
    private List<String> whatYouWillLearn;
    private List<String> requireToPass;

    public Course toEntity() {

        return Course.builder()
                .courseName(courseName)
                .description(description)
                .moreInformation(moreInformation)
                .image(image)
                .introductionVideoUrl(introductionVideoUrl)
                .totalDuration(totalDuration)
                .totalChapter(totalChapter)
                .totalLessons(totalLessons)
                .price(price)
                .rating(rating)
                .whatsLearned(whatYouWillLearn)
                .requireToPass(requireToPass)
                .build();
    }
}

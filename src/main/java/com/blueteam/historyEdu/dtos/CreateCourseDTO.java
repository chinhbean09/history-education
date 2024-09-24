package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Course;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCourseDTO {

    private String courseName;
    private String moreInformation;
    private String description;
    private String image;
    private String introductionVideoUrl;
    private Long totalDuration;
    private Long totalChapter;
    private Long totalVideos;
    private Long price;
    private Long rating;
    private List<String> whatYouWillLearn;
    private List<String> requireToPass;
    private List<ChapterDTO> chapters;

    public Course toEntity() {
        return Course.builder()
                .courseName(courseName)
                .description(description)
                .moreInformation(moreInformation)
                .image(image)
                .introductionVideoUrl(introductionVideoUrl)
                .totalDuration(totalDuration)
                .totalChapter(totalChapter)
                .totalVideos(totalVideos)
                .price(price)
                .rating(rating)
                .whatsLearned(whatYouWillLearn)
                .requireToPass(requireToPass)
                .build();
    }
}

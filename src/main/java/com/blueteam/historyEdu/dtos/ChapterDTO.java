package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChapterDTO {

    private Long id;

    private String chapterName;

    private String description;

    private String url;

    private Integer stt;

    private List<LessonDTO> lessons; // Add this field to update lessons


    public Chapter toEntity(Course course) {
        return Chapter.builder()
                .chapterName(chapterName)
                .stt(stt)
                .course(course) // Ensure course is set in the entity
                .lessons(new ArrayList<>())
                .build();
    }
}

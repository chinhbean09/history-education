package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Chapter;
import com.blueteam.historyEdu.entities.Course;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChapterDTO {
    @JsonProperty("chapterName")
    private String chapterName;
    private Integer stt;

//    @JsonProperty("courseId") // Add this to ensure course is associated
//    private Long courseId;

    public Chapter toEntity(Course course) {
        return Chapter.builder()
                .chapterName(chapterName)
                .stt(stt)
                .course(course) // Ensure course is set in the entity
                .lessons(new ArrayList<>())
                .build();
    }
}

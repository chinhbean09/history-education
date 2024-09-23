package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Chapter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterResponse {

    private Long id;
    private String chapterName;
    private List<LessonReponse> lesson;

    public static ChapterResponse fromChapter(Chapter chapter) {
        // Handle potential null videos
        List<LessonReponse> lesson = (chapter.getLessons() != null) ?
                chapter.getLessons().stream().map(LessonReponse::fromLesson).toList() :
                new ArrayList<>();

        return ChapterResponse.builder()
                .id(chapter.getId())
                .chapterName(chapter.getChapterName())
                .lesson(lesson)
                .build();
    }
}

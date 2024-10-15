package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.dtos.quiz.QuizDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonDTO {
    private Long id;
    private String lessonName;
    private String content;
    private List<VideoDTO> videos;
    private List<InformationDTO> informations;
    private List<QuizDTO> quizzes;
}

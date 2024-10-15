package com.blueteam.historyEdu.dtos.quiz;

import com.blueteam.historyEdu.dtos.QuestionDTO;
import com.blueteam.historyEdu.entities.Quiz;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import com.blueteam.historyEdu.entities.Question;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {
//    @NotBlank
    private String title;

    @JsonProperty("expiration-time")
    private int expirationTime;

    private int stt;

    @JsonProperty("lesson_id")
    private Long lessonId;

    private List<QuestionDTO> questions;

    public Quiz toEntity() {
        Quiz quiz = new Quiz();
        quiz.setTitle(this.title);
        quiz.setExpirationTime(this.expirationTime);
        quiz.setStt(this.stt);
        // If you have logic to set the lesson reference later, you can skip setting lessonId here.
        return quiz;
    }

}

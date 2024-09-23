package com.blueteam.historyEdu.dtos.quiz;

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
    @NotBlank
    private String title;

    @JsonProperty("expiration-time")
    private int expirationTime;

    @JsonProperty("chapter_id")
    private Long chapterId;

    private List<Question> questions;
}

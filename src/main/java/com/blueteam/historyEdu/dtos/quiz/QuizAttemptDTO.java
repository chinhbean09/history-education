package com.blueteam.historyEdu.dtos.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptDTO {
    @JsonProperty("quiz-id")
    private Long quizId;
    private Map<Long, String> answers; // Key: questionId, Value: user's answer
}

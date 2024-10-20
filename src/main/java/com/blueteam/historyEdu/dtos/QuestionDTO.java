package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Question;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String text;
    private List<String> answers;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String correctAnswer;

    public static QuestionDTO fromQuestion(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getText(),
                question.getAnswers(),
                null
        );
    }

    // Dùng correctAnswer khi chuyển thành entity
    public Question toEntity() {
        return Question.builder()
                .id(this.id)
                .text(this.text)
                .correctAnswer(this.correctAnswer)
                .answers(this.answers)
                .build();
    }
}


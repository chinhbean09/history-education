package com.blueteam.historyEdu.dtos.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultDTO {
    private int score;
    private boolean isPass;

}

package com.blueteam.historyEdu.dtos.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuizDTO {

    private String title;

    @JsonProperty("expiration-time")
    private int expirationTime;

    private int stt;
}

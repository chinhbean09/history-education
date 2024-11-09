package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.entities.Interaction;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InteractionDTO {
    private String comment;


    public Interaction toEntity() {
        return Interaction.builder()
                .content(comment)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

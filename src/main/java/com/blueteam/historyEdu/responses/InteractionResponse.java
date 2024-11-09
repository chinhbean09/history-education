package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Interaction;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InteractionResponse {

    private String userName;
    private String comment;
    private LocalDateTime createAt;
    private Long videoId;

    public static InteractionResponse fromInteraction(Interaction interaction) {
        return InteractionResponse.builder()
                .comment(interaction.getContent())
                .userName(interaction.getUser().getFullName())
                .createAt(interaction.getCreatedAt())
                .videoId(interaction.getVideo().getId())
                .build();
    }
}

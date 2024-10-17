package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoProgressDTO {
    private Long videoId;
    private String videoName;
    private Double watchedDuration;
    private Double duration;
    private boolean isCompleted;
}


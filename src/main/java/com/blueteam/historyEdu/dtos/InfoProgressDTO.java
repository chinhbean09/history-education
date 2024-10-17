package com.blueteam.historyEdu.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoProgressDTO {
    private Long infoId;
    private String infoName;
    private boolean isViewed;
}

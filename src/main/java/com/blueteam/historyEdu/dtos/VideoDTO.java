package com.blueteam.historyEdu.dtos;


import com.blueteam.historyEdu.entities.Video;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VideoDTO {

    private String videoName;
    private String type;
    private String lessonVideo;
    private Integer duration;
    private String moreInformation;
    private List<String> supportingMaterials;

    public Video toEntity() {
        return Video.builder()
                .videoName(videoName)
                .type(type)
                .lessonVideo(lessonVideo)
                .moreInformation(moreInformation)
                .supportingMaterials(supportingMaterials)
                .duration(duration)
                .build();
    }
}

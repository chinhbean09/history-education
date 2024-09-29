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
    private String lessonVideo;
    private Integer duration;
    private Integer stt;
    private String moreInformation;
    private List<String> supportingMaterials;

    public Video toEntity() {
        return Video.builder()
                .videoName(videoName)
                .lessonVideo(lessonVideo)
                .moreInformation(moreInformation)
                .stt(stt)
                .supportingMaterials(supportingMaterials)
                .duration(duration)
                .build();
    }
}

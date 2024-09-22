package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Video;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResponse {

    private Long videoId;
    private String type;
    private String videoName;
    private String lessonVideo;
    private Integer duration;
    private String moreInformation;
    private List<String> supportingMaterials;

    public static VideoResponse fromVideo(Video video) {
        return VideoResponse.builder()
                .videoId(video.getId())
                .type(video.getType())
                .videoName(video.getVideoName())
                .lessonVideo(video.getLessonVideo())
                .moreInformation(video.getMoreInformation())
                .duration(video.getDuration())
                .supportingMaterials(video.getSupportingMaterials())
                .build();
    }
}

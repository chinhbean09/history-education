package com.blueteam.historyEdu.responses;

import com.blueteam.historyEdu.entities.Video;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResponse {

    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String moreInformation;

    public static VideoResponse fromVideo(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .moreInformation(video.getMoreInformation())
                .build();
    }
}

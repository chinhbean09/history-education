package com.blueteam.historyEdu.entities;

import com.blueteam.historyEdu.entities.convert.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_name")
    private String videoName;

//    @Column(name = "description")
//    private String description;

//    @Column(name = "type")
//    private String type;

    @Column(name = "lesson_video")
    private String lessonVideo;

    @Column(name = "duration")
    private Integer duration;

//    @Column(name = "create_at")
//    private LocalDateTime createAt;

    @Column(name="more_information")
    private String moreInformation;

    @Convert(converter = StringListConverter.class)
    @Column(name = "supporting_materials", columnDefinition = "TEXT", nullable = false)
    private List<String> supportingMaterials;

//    @ManyToOne
//    @JoinColumn(name = "chapter_id", nullable = false)
//    private Chapter chapter;  // Correctly sets chapter_id in videos
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}


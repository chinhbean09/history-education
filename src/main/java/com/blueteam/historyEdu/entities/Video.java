package com.blueteam.historyEdu.entities;

import com.blueteam.historyEdu.entities.common.ItemWithStt;
import com.blueteam.historyEdu.entities.convert.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video implements ItemWithStt {

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

    @Column(name = "stt")
    private Integer stt;

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

    @OneToMany(mappedBy="video", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<VideoProgress> videoProgresses = new ArrayList<>();

    @Override
    public void setStt(int stt) {
        this.stt = stt;
    }

    @Override
    public int getStt() {
        return stt;
    }
}


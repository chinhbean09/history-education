package com.blueteam.historyEdu.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chapters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String chapterName;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "url", nullable = true)
    private String url;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;  // Should not cause video_id to appear in chapters

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}

package com.blueteam.historyEdu.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    @Column(name = "enrollmentCount", nullable = false)
//    private Long enrollmentCount;

    @Column(name="course_name", nullable = false)
    private String courseName;

    @Column(name = "introduction_video", nullable = false)
    private String introductionVideoUrl;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "totalDuration", nullable = false)
    private Long totalDuration;

    @Column(name = "total-chapter", nullable = false)
    private Long totalChapter;

    @Column(name = "total-videos", nullable = false)
    private Long totalVideos;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "rating", nullable = false)
    private Long rating;

    @Column(name = "whats_learned")
    @ElementCollection
    private List<String> whatsLearned;

    @Column(name = "requirement")
    @ElementCollection
    private List<String> requireToPass;


    @OneToMany(mappedBy = "course")
    private List<UserProgress> userProgressList;

    @OneToMany(mappedBy = "course")
    private List<User> users;

}

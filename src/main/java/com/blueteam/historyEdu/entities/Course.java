package com.blueteam.historyEdu.entities;
import com.blueteam.historyEdu.entities.convert.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Column(name = "introduction_video", nullable = true)
    private String introductionVideoUrl;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "more_information", nullable = false)
    private String moreInformation;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "total_duration", nullable = true)
    private Long totalDuration;

    @Column(name = "total_chapter", nullable = true)
    private Long totalChapter;

    @Column(name = "total_lessons", nullable = true)
    private Long totalLessons;

    @Column(name = "price", nullable = true)
    private Long price;

    @Column(name = "rating", nullable = false)
    private Long rating;

    @Convert(converter = StringListConverter.class)
    @Column(name = "whats_learned", columnDefinition = "TEXT", nullable = false)
    private List<String> whatsLearned;

    @Convert(converter = StringListConverter.class)
    @Column(name = "requirement", columnDefinition = "TEXT", nullable = false)
    private List<String> requireToPass;

    // Initialize the lists to avoid null pointer issues
    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Chapter> chapters = new ArrayList<>();

//    @OneToMany(mappedBy = "course")
//    private List<UserProgress> userProgressList = new ArrayList<>();

//    @OneToMany(mappedBy = "course")
//    private List<User> users = new ArrayList<>();

}

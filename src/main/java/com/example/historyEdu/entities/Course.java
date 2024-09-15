package com.example.historyEdu.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
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

    @Column(name = "enrollmentCount", nullable = false)
    private Long enrollmentCount;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "totalDuration", nullable = false)
    private Long totalDuration;

    @Column(name = "totalChapter", nullable = false)
    private Long totalChapter;

    @Column(name = "totalVideos", nullable = false)
    private Long totalVideos;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "rating", nullable = false)
    private Long rating;

    @OneToMany(mappedBy = "course")
    private List<UserProgress> userProgressList;

    @OneToMany(mappedBy = "course")
    private List<User> users;

}

package com.blueteam.historyEdu.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "quizzes")  // Exclude quizzes to prevent recursion
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

//    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Video> videos;  // Should not cause video_id to appear in chapters

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons =new ArrayList<>();

}

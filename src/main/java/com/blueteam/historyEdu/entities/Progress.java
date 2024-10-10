package com.blueteam.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;  // Thêm quan hệ với Course

    @Column(name = "is_chapter_completed")
    private boolean isChapterCompleted;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoProgress> videoProgresses;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizProgress> quizProgresses;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InfoProgress> infoProgresses;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

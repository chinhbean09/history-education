package com.example.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "package_id", columnDefinition = "bigint")
    private ServicePackage packageId;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "role_id", columnDefinition = "bigint")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "interaction_id", columnDefinition = "bigint")
    private Interaction interaction;

    @ManyToOne
    @JoinColumn(name = "quiz_attempt_id", columnDefinition = "bigint")
    private QuizAttempt quizAttempt;

    @Column(name = "package_start_date")
    private LocalDate packageStartDate;

    @Column(name = "package_end_date")
    private LocalDate packageEndDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "facebook_account_id")
    private String facebookAccountId;

    @Column(name = "google_account_id")
    private String googleAccountId;

    @Column(name = "avatar")
    private String avatar;

//    @OneToMany(mappedBy = "user")
//    private List<VideoProgress> videoProgressList;
//
    @OneToMany(mappedBy = "user")
    private List<UserProgress> userProgressList;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

}

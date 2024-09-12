package com.example.historyEdu.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password")
    private String password;

    @Column(name = "question")
    private String question;

    @Column(name = "expiration_date")
    private LocalDateTime expirationTime;

    @Column(name = "is_verified")
    private boolean verified;
}

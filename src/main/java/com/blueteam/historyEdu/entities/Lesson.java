package com.blueteam.historyEdu.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "lessons")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Information> informations;
}

package com.blueteam.historyEdu.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "packages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "price")
    private Double price;

    @Column(name = "duration")
    private Integer duration; // duration in days

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "servicePackage")
    private List<Purchase> purchases;
}

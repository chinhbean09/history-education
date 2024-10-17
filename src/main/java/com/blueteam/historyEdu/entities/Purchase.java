package com.blueteam.historyEdu.entities;

import com.blueteam.historyEdu.enums.PackageStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", nullable = false)
    private Long orderCode;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private ServicePackage servicePackage;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PackageStatus packageStatus;

    @Column(name = "price", nullable = false)
    private Double price;

}

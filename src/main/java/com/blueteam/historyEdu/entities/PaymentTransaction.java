    package com.blueteam.historyEdu.entities;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "payment_transactions")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class PaymentTransaction {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column
        private String transactionCode;

        @ManyToOne
        @JoinColumn(name = "package_id")
        @JsonBackReference("service-package-payment")
        private ServicePackage servicePackage;

        @Column(nullable = false)
        private String phoneGuest;

        @Column(nullable = false)
        private String nameGuest;

        @Column(nullable = false)
        private String emailGuest;

        @Column(nullable = false)
        private LocalDateTime createDate;

    }

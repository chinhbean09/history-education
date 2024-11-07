package com.blueteam.historyEdu.dtos;

import com.blueteam.historyEdu.enums.PackageStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDTO {
    private Long id;
    private Long orderCode;
    private String userName;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private PackageStatus packageStatus;
    private Double price;
}

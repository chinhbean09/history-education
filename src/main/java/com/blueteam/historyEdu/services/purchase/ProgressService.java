package com.blueteam.historyEdu.services.purchase;

import com.blueteam.historyEdu.dtos.PurchaseDTO;
import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.repositories.IPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService implements IPurchaseService{

    private final IPurchaseRepository purchaseRepository;


    public List<PurchaseDTO> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return purchases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PurchaseDTO convertToDTO(Purchase purchase) {
        return PurchaseDTO.builder()
                .id(purchase.getId())
                .orderCode(purchase.getOrderCode())
                .userName(purchase.getUser().getName())
                .purchaseDate(purchase.getPurchaseDate())
                .expiryDate(purchase.getExpiryDate())
                .packageStatus(purchase.getPackageStatus())
                .price(purchase.getPrice())
                .build();
    }


}

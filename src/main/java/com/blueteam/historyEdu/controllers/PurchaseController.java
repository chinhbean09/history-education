package com.blueteam.historyEdu.controllers;


import com.blueteam.historyEdu.dtos.PurchaseDTO;
import com.blueteam.historyEdu.services.purchase.IPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/purchase")
@RequiredArgsConstructor

public class PurchaseController {
    private final IPurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }



}

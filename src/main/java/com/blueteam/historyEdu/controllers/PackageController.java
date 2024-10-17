package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.repositories.IServicePackageRepository;
import com.blueteam.historyEdu.services.servicepackage.IPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/packages")
public class PackageController {

    private final IServicePackageRepository servicePackageRepository;
    private final IPackageService servicePackageService;

    @GetMapping
    public ResponseEntity<List<ServicePackage>> getAllPackages() {
        List<ServicePackage> packages = servicePackageService.getAllPackages();
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ServicePackage getPackageById(@PathVariable Long id) {
        return servicePackageService.getPackageById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ServicePackage> createPackage(@RequestBody ServicePackage servicePackage) {
        ServicePackage createdPackage = servicePackageService.createPackage(servicePackage);
        return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ServicePackage> updatePackage(@PathVariable Long id, @RequestBody ServicePackage servicePackage) {
        ServicePackage updatedPackage = servicePackageService.updatePackage(id, servicePackage);
        return new ResponseEntity<>(updatedPackage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        servicePackageService.deletePackage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.responses.ResponseObject;
import com.blueteam.historyEdu.services.servicepackage.IPackageService;
import com.blueteam.historyEdu.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
public class ServicePackageController {

    private final IPackageService packageService;

    @GetMapping("/get-all-package")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> getAllPackages() {
        try {
            List<ServicePackage> packages = packageService.getAllPackages();
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(packages)
                            .message(MessageKeys.RETRIEVED_ALL_PACKAGES_SUCCESSFULLY)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.RETRIEVED_ALL_PACKAGES_FAILED)
                            .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> getPackageById(@PathVariable Long id) {
        try {
            ServicePackage servicePackage = packageService.getPackageById(id);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(servicePackage)
                            .message(MessageKeys.RETRIEVED_PACKAGE_DETAIL_SUCCESSFULLY)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.RETRIEVED_PACKAGE_DETAIL_FAILED)
                            .build());
        }
    }

    @PostMapping("/create-package")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createPackage(@Valid @RequestBody ServicePackage servicePackage, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(MessageKeys.INVALID_PACKAGE_CREATE_REQUEST)
                                .build());
            }

            ServicePackage createdPackage = packageService.createPackage(servicePackage);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(createdPackage)
                            .message(MessageKeys.CREATE_PACKAGE_SUCCESSFULLY)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.INVALID_PACKAGE_CREATE_REQUEST)
                            .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updatePackage(@PathVariable Long id, @Valid @RequestBody ServicePackage servicePackage, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message(MessageKeys.INVALID_PACKAGE_UPDATE_REQUEST)
                                .build());
            }

            ServicePackage updatedPackage = packageService.updatePackage(id, servicePackage);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(updatedPackage)
                            .message(MessageKeys.UPDATE_PACKAGE_SUCCESSFULLY)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(MessageKeys.PACKAGE_NOT_FOUND)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.INVALID_PACKAGE_UPDATE_REQUEST)
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deletePackage(@PathVariable Long id) {
        try {
            packageService.deletePackage(id);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(MessageKeys.DELETE_PACKAGE_SUCCESSFULLY)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(MessageKeys.PACKAGE_NOT_FOUND)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.DELETE_PACKAGE_FAILED)
                            .build());
        }
    }

    @PostMapping("/register-package/{packageId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ResponseObject> registerPackage(@PathVariable Long packageId) {
        try {
            ServicePackage servicePackage = packageService.registerPackage(packageId);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(MessageKeys.REGISTER_PACKAGE_SUCCESSFULLY)
                            .data(servicePackage)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(MessageKeys.PACKAGE_NOT_FOUND)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.REGISTER_PACKAGE_FAILED)
                            .build());
        }
    }

    @PostMapping("/check-package-expiration")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> checkPackageExpiration() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            if (currentUser.getServicePackage() == null) {
                return ResponseEntity.ok().body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message(MessageKeys.USER_DOES_NOT_HAVE_PACKAGE)
                                .build());
            }
            boolean isExpired = packageService.checkAndHandlePackageExpiration();
            String message = isExpired ? "Package expired and reset" : "Package is still valid";
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(message)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message(MessageKeys.PACKAGE_NOT_FOUND)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message(MessageKeys.PACKAGE_EXPIRED)
                            .build());
        }
    }

    @PutMapping("/update-status/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updatePackageStatus(@PathVariable Long userId, @RequestParam PackageStatus newStatus) throws PermissionDenyException {

        packageService.updatePackageStatus(userId, newStatus);
        return ResponseEntity.ok(MessageKeys.UPDATE_PACKAGE_SUCCESSFULLY);
    }




}

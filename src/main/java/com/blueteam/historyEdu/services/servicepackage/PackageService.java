package com.blueteam.historyEdu.services.servicepackage;

import com.blueteam.historyEdu.dtos.DataMailDTO;
import com.blueteam.historyEdu.entities.Role;
import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IServicePackageRepository;
import com.blueteam.historyEdu.repositories.IPaymentTransactionRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.services.sendmails.IMailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PackageService implements IPackageService {

    private final com.blueteam.historyEdu.repositories.IServicePackageRepository IServicePackageRepository;
    private final IUserRepository userRepository;
    private final IMailService mailService;
    private final IPaymentTransactionRepository IPaymentTransactionRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    @Override
    public List<ServicePackage> getAllPackages() {
        return IServicePackageRepository.findAll();
    }

    @Transactional
    @Override
    public ServicePackage getPackageById(Long id) {
        return IServicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package with ID: " + id + " does not exist."));
    }

    @Transactional
    @Override
    public ServicePackage createPackage(ServicePackage servicePackage) {
        validatePackage(servicePackage);
        return IServicePackageRepository.save(servicePackage);
    }

    @Transactional
    @Override

    public ServicePackage updatePackage(Long id, ServicePackage updatedPackage) {
        ServicePackage existingPackage = getPackageById(id);
        existingPackage.setPackageName(updatedPackage.getPackageName());
        existingPackage.setDescription(updatedPackage.getDescription());
        existingPackage.setPrice(updatedPackage.getPrice());
        existingPackage.setDuration(updatedPackage.getDuration());
        return IServicePackageRepository.save(existingPackage);
    }

    @Transactional
    @Override
    public void deletePackage(Long id) {
        ServicePackage existingPackage = getPackageById(id);
        IServicePackageRepository.delete(existingPackage);
    }

    private void validatePackage(ServicePackage servicePackage) {
        if (servicePackage.getPackageName() == null || servicePackage.getPackageName().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }

        if (servicePackage.getPrice() == null || servicePackage.getPrice() <= 0) {
            throw new IllegalArgumentException("Package price must be greater than 0");
        }

        if (servicePackage.getDuration() == null) {
            throw new IllegalArgumentException("Package duration must be greater than 0");
        }

        if (servicePackage.getDuration() > 365) {
            throw new IllegalArgumentException("Package duration cannot exceed 365 months");
        }

        if (servicePackage.getDuration() < 30) {
            throw new IllegalArgumentException("Package duration must be at least 30 month");
        }
    }

    @Transactional
    @Override

    public ServicePackage registerPackage(Long packageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ServicePackage servicePackage = IServicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        LocalDate now = LocalDate.now();

        if (servicePackage.getDuration() == 30) {
            currentUser.setServicePackage(servicePackage);
            currentUser.setPackageStartDate(now);
            currentUser.setPackageEndDate(now.plusDays(30));
            currentUser.setStatus(PackageStatus.PENDING);
            userRepository.save(currentUser);
        }
        return servicePackage;
    }

    @Transactional
    @Override
    public boolean checkAndHandlePackageExpiration() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        currentUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate now = LocalDate.now();

        if (currentUser.getPackageEndDate() != null && now.isAfter(currentUser.getPackageEndDate())) {
            currentUser.setServicePackage(null);
            currentUser.setPackageStartDate(null);
            currentUser.setPackageEndDate(null);
            userRepository.save(currentUser);
            return true;
        }
        return false;
    }

    @Override
    public void sendMailNotificationForPackagePayment(ServicePackage servicePackage, String email) {
    }

    @Override
    public ServicePackage findPackageWithPaymentTransactionById(Long packageId) {
        return IServicePackageRepository.findPackageWithPaymentTransactionById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
    }

    @Transactional
    public void updatePackageStatus(Long userId, PackageStatus newStatus) throws PermissionDenyException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getServicePackage() == null) {
            throw new IllegalArgumentException("User does not have an package");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ServicePackage servicePackage = user.getServicePackage();
        if (servicePackage == null) {
            throw new IllegalArgumentException("User does not have an active package");
        }
        if (Role.ADMIN.equals(currentUser.getRole().getRoleName())) {

            switch (newStatus) {
                case ACTIVE, INACTIVE, PENDING, EXPIRED:
                    user.setStatus(newStatus);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid booking status");
            }
        } else {
            throw new PermissionDenyException("You do not have permission to update the package status.");
        }
        user.setStatus(newStatus);
        userRepository.save(user);
    }


}

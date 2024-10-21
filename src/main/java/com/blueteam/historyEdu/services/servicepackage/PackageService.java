package com.blueteam.historyEdu.services.servicepackage;

import com.blueteam.historyEdu.dtos.DataMailDTO;
import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.entities.Role;
import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;
import com.blueteam.historyEdu.repositories.IPurchaseRepository;
import com.blueteam.historyEdu.repositories.IServicePackageRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.services.sendmails.IMailService;
import com.blueteam.historyEdu.utils.MailTemplate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final IPurchaseRepository purchaseRepository;


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

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredPackages() {
        LocalDateTime now = LocalDateTime.now();

        List<Purchase> expiredPurchases = purchaseRepository.findAllByPackageStatusAndExpiryDateBefore(PackageStatus.PAID, now);

        for (Purchase purchase : expiredPurchases) {
            purchase.setPackageStatus(PackageStatus.EXPIRED);
            purchaseRepository.save(purchase);

            User user = purchase.getUser();
            user.setPackageStatus(PackageStatus.EXPIRED);
            userRepository.save(user);

            sendExpirationNotification(user, purchase);
        }
    }

    private void sendExpirationNotification(User user, Purchase purchase) {

        try {
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo(user.getEmail());
            dataMail.setSubject(MailTemplate.SEND_MAIL_SUBJECT.PACKAGE_EXPIRED);

            Map<String, Object> properties = new HashMap<>();
            properties.put("fullName", user.getFullName());
            properties.put("expiryDate", purchase.getExpiryDate());

            dataMail.setProps(properties);
            mailService.sendHtmlMail(dataMail, MailTemplate.SEND_MAIL_TEMPLATE.PACKAGE_EXPIRED_TEMPLATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

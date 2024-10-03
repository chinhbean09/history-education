package com.blueteam.historyEdu.services.servicepackage;
import com.blueteam.historyEdu.entities.ServicePackage;
import java.util.List;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.exceptions.PermissionDenyException;

public interface IPackageService {
    List<ServicePackage> getAllPackages();

    ServicePackage getPackageById(Long id);

    ServicePackage createPackage(ServicePackage servicePackage);

    ServicePackage updatePackage(Long id, ServicePackage servicePackage);

    void deletePackage(Long id);

    ServicePackage registerPackage(Long packageId);

    boolean checkAndHandlePackageExpiration();

    void sendMailNotificationForPackagePayment(ServicePackage servicePackage, String email);

    ServicePackage findPackageWithPaymentTransactionById(Long packageId);

    void updatePackageStatus(Long userId, PackageStatus newStatus) throws PermissionDenyException;
}

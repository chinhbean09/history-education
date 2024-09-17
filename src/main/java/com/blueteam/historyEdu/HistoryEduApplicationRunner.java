package com.blueteam.historyEdu;
import com.blueteam.historyEdu.entities.Role;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.repositories.IRoleRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class HistoryEduApplicationRunner implements ApplicationRunner {
    @Autowired
    private com.blueteam.historyEdu.repositories.IUserRepository IUserRepository;

    @Autowired
    private com.blueteam.historyEdu.repositories.IRoleRepository IRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${historyedu.admin.email}")
    private String email;

    @Value("${historyedu.guest.email}")
    private String guestEmail;

    @Value("${historyedu.admin.fullName}")
    private String fullName;

    @Value("${historyedu.guest.fullName}")
    private String guestFullName;

    @Value("${historyedu.admin.address}")
    private String address;

    @Value("${historyedu.guest.address}")
    private String guestAddress;

    @Value("${historyedu.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${historyedu.guest.phoneNumber}")
    private String guestPhoneNumber;


    @Value("${historyedu.admin.gender}")
    private String gender;

    @Value("${historyedu.admin.password}")
    private String password;

    @Value("${historyedu.admin.active}")
    private Boolean active;

    @Override
    public void run(ApplicationArguments args) {
        Optional<User> findAccountResult = IUserRepository.findByPhoneNumber(phoneNumber);
        Optional<Role> existRolePermission = IRoleRepository.findById((long) 1);
        Optional<User> findAccountGuest = IUserRepository.findByPhoneNumber(guestPhoneNumber);


        Role AdminRole = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .build();
        Role CustomerRole = Role.builder()
                .id(2L)
                .roleName("CUSTOMER")
                .build();

        if (existRolePermission.isEmpty()) {
            System.out.println("There is no role Initialing...!");
        }

        IRoleRepository.save(AdminRole);
        IRoleRepository.save(CustomerRole);


        if (findAccountResult.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setEmail(email);
            user.setGender(gender);
            user.setAddress(address);
            user.setPassword(encodedPassword);
            user.setActive(active);
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setRole(AdminRole);
            user.setActive(true);
            user.setDateOfBirth(new Date());
            IUserRepository.save(user);
            System.out.println("Admin initialized!");
        }

        if (findAccountGuest.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setEmail(guestEmail);
            user.setGender(gender);
            user.setAddress(guestAddress);
            user.setPassword(encodedPassword);
            user.setActive(active);
            user.setFullName(guestFullName);
            user.setPhoneNumber(guestPhoneNumber);
            user.setRole(CustomerRole);
            user.setActive(true);
            user.setDateOfBirth(new Date());
            IUserRepository.save(user);
            System.out.println("CUSTOMER initialized!");
        }

        System.out.println("Hello, I'm System Manager!");
    }
}

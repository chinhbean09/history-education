package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phone);

    Optional<User> findByFullName(String fullName);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}

package com.example.historyEdu.repositories;

import com.example.historyEdu.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}

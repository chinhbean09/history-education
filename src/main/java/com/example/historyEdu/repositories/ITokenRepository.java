package com.example.historyEdu.repositories;

import com.example.historyEdu.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITokenRepository extends JpaRepository<Token, Long> {
}

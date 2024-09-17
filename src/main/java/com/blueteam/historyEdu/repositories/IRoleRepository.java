package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String roleUser);
}

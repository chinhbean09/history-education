package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    // get all interactions by video Id

    List<Interaction> findAllByVideoId(Long videoId);
}

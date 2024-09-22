package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVideoRepository extends JpaRepository<Video, Long> {
}

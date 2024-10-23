package com.blueteam.historyEdu.repositories;

import com.blueteam.historyEdu.entities.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IVideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VideoProgress vp WHERE vp.video.id = :videoId")
    void deleteByVideoId(@Param("videoId") Long videoId);
}

package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.ApplicationHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationHistoryRepository
        extends JpaRepository<ApplicationHistory, UUID> {

    @EntityGraph(attributePaths = {"actionByUser", "assignedToUser"})
    List<ApplicationHistory> findAllByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}

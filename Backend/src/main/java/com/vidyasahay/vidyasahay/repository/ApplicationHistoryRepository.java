package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.ApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationHistoryRepository
        extends JpaRepository<ApplicationHistory, UUID> {
}
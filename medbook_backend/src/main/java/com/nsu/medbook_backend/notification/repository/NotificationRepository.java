package com.nsu.medbook_backend.notification.repository;

import com.nsu.medbook_backend.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndReadStatusOrderByCreatedAtDesc(Long userId, boolean read);
    long countByUserIdAndReadStatus(Long userId, boolean read);
}

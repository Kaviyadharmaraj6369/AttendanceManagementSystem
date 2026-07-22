package com.example.attendancemanagementsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsernameAndRoleOrderByCreatedAtDesc(String username, String role);
    long countByUsernameAndRoleAndIsReadFalse(String username, String role);
}
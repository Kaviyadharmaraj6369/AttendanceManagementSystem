package com.example.attendancemanagementsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByStudentUsername(String studentUsername);
    List<LeaveApplication> findByStatus(String status);
    List<LeaveApplication> findByStudentUsernameAndStatus(String studentUsername, String status);
    List<LeaveApplication> findAllByOrderByAppliedDateDesc(); // <-- Idhu add pannu
}
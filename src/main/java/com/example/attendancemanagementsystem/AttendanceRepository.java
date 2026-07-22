package com.example.attendancemanagementsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentUsername(String studentUsername);
    List<Attendance> findByDate(java.time.LocalDate date);
    List<Attendance> findByStudentUsernameAndDate(String studentUsername, java.time.LocalDate date);
}
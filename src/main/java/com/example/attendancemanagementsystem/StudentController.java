package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ---------- Page routes (just serve the static shell; data is loaded by JS via the /api endpoints below) ----------

    @GetMapping("/student-dashboard")
    public String studentDashboard(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return "redirect:/student-login";
        }
        return "forward:/student-dashboard.html";
    }

    @GetMapping("/student-profile")
    public String studentProfile(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return "redirect:/student-login";
        }
        return "forward:/student-profile.html";
    }

    // ---------- JSON APIs consumed by the pages above ----------

    @GetMapping("/api/student/dashboard-stats")
    @ResponseBody
    public Map<String, Object> studentDashboardStats(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return result;
        }

        String username = (String) session.getAttribute("studentUsername");
        List<Attendance> attendances = attendanceRepository.findByStudentUsername(username);

        long totalClasses = attendances.size();
        long present = attendances.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent = attendances.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        long late = attendances.stream().filter(a -> "Late".equals(a.getStatus())).count();
        double attendancePercentage = totalClasses > 0 ? (present * 100.0 / totalClasses) : 0;

        result.put("totalClasses", totalClasses);
        result.put("present", present);
        result.put("absent", absent);
        result.put("late", late);
        result.put("attendancePercentage", String.format("%.1f", attendancePercentage));
        result.put("lowAttendance", totalClasses > 0 && attendancePercentage < 75);
        return result;
    }

    @GetMapping("/api/attendance/my")
    @ResponseBody
    public List<Attendance> myAttendance(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return List.of();
        }
        String username = (String) session.getAttribute("studentUsername");
        return attendanceRepository.findByStudentUsername(username);
    }

    // ---------- Staff-only student management ----------

    @GetMapping("/api/students")
    @ResponseBody
    public List<Student> allStudents(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return List.of();
        }
        return studentRepository.findAll();
    }

    @PostMapping("/add-student")
    public String addStudent(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String department,
                             @RequestParam String year,
                             @RequestParam String section) {
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(PasswordUtil.encode(password));
        student.setName(name);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDepartment(department);
        student.setYear(year);
        student.setSection(section);
        studentRepository.save(student);
        return "redirect:/students";
    }

    @PostMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students";
    }
}
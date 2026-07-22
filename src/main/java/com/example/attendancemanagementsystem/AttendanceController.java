package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LoginLogRepository loginLogRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        long totalStudents = studentRepository.count();
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus().equals("Present")).count();
        long absentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus().equals("Absent")).count();

        model.addAttribute("staffName", session.getAttribute("staffName"));
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("presentToday", presentToday);
        model.addAttribute("absentToday", absentToday);

        return "forward:/staffdashboard.html";
    }

    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        model.addAttribute("staffName", session.getAttribute("staffName"));

        return "forward:/attendance.html";
    }

    @PostMapping("/mark-attendance")
    public String markAttendance(@RequestParam String studentUsername,
                                 @RequestParam String date,
                                 @RequestParam String status,
                                 @RequestParam String subject,
                                 @RequestParam(required = false) String remarks,
                                 HttpSession session) {
        Attendance attendance = new Attendance();
        attendance.setStudentUsername(studentUsername);

        Student student = studentRepository.findByUsername(studentUsername).orElse(null);
        if (student != null) {
            attendance.setStudentId(student.getId());
            attendance.setStudentName(student.getName());
        }

        attendance.setDate(LocalDate.parse(date));
        attendance.setStatus(status);
        attendance.setSubject(subject);
        attendance.setRemarks(remarks);
        attendance.setPostedBy((String) session.getAttribute("staffName"));
        attendance.setPostedDate(LocalDate.now());

        attendanceRepository.save(attendance);
        return "redirect:/attendance";
    }

    @GetMapping("/students")
    public String students(HttpSession session, Model model) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        model.addAttribute("staffName", session.getAttribute("staffName"));

        return "forward:/students.html";
    }

    @GetMapping("/report")
    public String report(HttpSession session, Model model) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        List<Attendance> attendances = attendanceRepository.findAll();
        model.addAttribute("attendances", attendances);
        model.addAttribute("staffName", session.getAttribute("staffName"));

        return "forward:/report.html";
    }

    @GetMapping("/login-logs")
    public String loginLogs(HttpSession session, Model model) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        List<LoginLog> logs = loginLogRepository.findAllByOrderByLoginTimeDesc();
        model.addAttribute("logs", logs);
        model.addAttribute("staffName", session.getAttribute("staffName"));

        return "forward:/login-logs.html";
    }

    @GetMapping("/export-attendance")
    public void exportAttendance(HttpServletResponse response, HttpSession session) throws Exception {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            response.sendRedirect("/staff-login");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_report.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Student Name,Username,Date,Status,Subject,Remarks,Posted By");

        List<Attendance> attendances = attendanceRepository.findAll();
        for (Attendance a : attendances) {
            writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                    a.getId(), a.getStudentName(), a.getStudentUsername(),
                    a.getDate(), a.getStatus(), a.getSubject(),
                    a.getRemarks() != null ? a.getRemarks() : "", a.getPostedBy()));
        }
        writer.flush();
    }

    @GetMapping("/api/check-session")
    @ResponseBody
    public Map<String, Object> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String role = (String) session.getAttribute("role");

        if (role != null) {
            response.put("loggedIn", true);
            response.put("role", role);
            if (role.equals("staff")) {
                response.put("name", session.getAttribute("staffName"));
            } else {
                response.put("name", session.getAttribute("studentName"));
            }
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }

    @GetMapping("/api/attendance/all")
    @ResponseBody
    public List<Attendance> allAttendance(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return List.of();
        }
        return attendanceRepository.findAll();
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStats(HttpSession session) {
        Map<String, Object> stats = new HashMap<>();

        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            stats.put("totalStudents", 0);
            stats.put("presentToday", 0);
            stats.put("absentToday", 0);
            return stats;
        }

        long totalStudents = studentRepository.count();
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus().equals("Present")).count();
        long absentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus().equals("Absent")).count();

        stats.put("totalStudents", totalStudents);
        stats.put("presentToday", presentToday);
        stats.put("absentToday", absentToday);
        return stats;
    }
}
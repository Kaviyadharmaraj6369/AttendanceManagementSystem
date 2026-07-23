package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ---------- Page ----------

    @GetMapping("/profile")
    public String profilePage(HttpSession session) {
        if (session.getAttribute("role") == null) {
            return "redirect:/";
        }
        return "forward:/profile.html";
    }

    // ---------- Combined JSON profile (staff or student) ----------

    @GetMapping("/api/profile")
    @ResponseBody
    public Map<String, Object> currentProfile(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        String role = (String) session.getAttribute("role");
        if (role == null) {
            return result;
        }

        if ("staff".equals(role)) {
            String username = (String) session.getAttribute("staffUsername");
            Staff staff = staffRepository.findByUsername(username).orElse(null);
            if (staff != null) {
                result.put("role", "staff");
                result.put("username", staff.getUsername());
                result.put("name", staff.getName());
                result.put("email", staff.getEmail());
                result.put("phone", staff.getPhone());
                result.put("department", staff.getDepartment());
            }
        } else if ("student".equals(role)) {
            String username = (String) session.getAttribute("studentUsername");
            Student student = studentRepository.findByUsername(username).orElse(null);
            if (student != null) {
                result.put("role", "student");
                result.put("username", student.getUsername());
                result.put("name", student.getName());
                result.put("email", student.getEmail());
                result.put("phone", student.getPhone());
                result.put("department", student.getDepartment());
                result.put("year", student.getYear());
                result.put("section", student.getSection());
            }
        }
        return result;
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestParam String name,
                                             @RequestParam String email,
                                             @RequestParam(required = false) String phone,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String role = (String) session.getAttribute("role");

        if ("staff".equals(role)) {
            String username = (String) session.getAttribute("staffUsername");
            Staff staff = staffRepository.findByUsername(username).orElse(null);
            if (staff == null) {
                response.put("success", false);
                response.put("message", "Staff account not found.");
                return response;
            }
            staff.setName(name);
            staff.setEmail(email);
            staff.setPhone(phone);
            staffRepository.save(staff);
            session.setAttribute("staffName", name);
        } else if ("student".equals(role)) {
            String username = (String) session.getAttribute("studentUsername");
            Student student = studentRepository.findByUsername(username).orElse(null);
            if (student == null) {
                response.put("success", false);
                response.put("message", "Student account not found.");
                return response;
            }
            student.setName(name);
            student.setEmail(email);
            student.setPhone(phone);
            studentRepository.save(student);
            session.setAttribute("studentName", name);
        } else {
            response.put("success", false);
            response.put("message", "Not logged in.");
            return response;
        }

        response.put("success", true);
        response.put("message", "Profile updated successfully.");
        return response;
    }

    @PostMapping("/change-password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestParam String oldPassword,
                                              @RequestParam String newPassword,
                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String role = (String) session.getAttribute("role");

        if ("staff".equals(role)) {
            String username = (String) session.getAttribute("staffUsername");
            Staff staff = staffRepository.findByUsername(username).orElse(null);
            if (staff == null || !PasswordUtil.matches(oldPassword, staff.getPassword())) {
                response.put("success", false);
                response.put("message", "Current password is incorrect.");
                return response;
            }
            staff.setPassword(PasswordUtil.encode(newPassword));
            staffRepository.save(staff);
        } else if ("student".equals(role)) {
            String username = (String) session.getAttribute("studentUsername");
            Student student = studentRepository.findByUsername(username).orElse(null);
            if (student == null || !PasswordUtil.matches(oldPassword, student.getPassword())) {
                response.put("success", false);
                response.put("message", "Current password is incorrect.");
                return response;
            }
            student.setPassword(PasswordUtil.encode(newPassword));
            studentRepository.save(student);
        } else {
            response.put("success", false);
            response.put("message", "Not logged in.");
            return response;
        }

        response.put("success", true);
        response.put("message", "Password changed successfully.");
        return response;
    }
}
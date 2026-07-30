package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class LoginController {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LoginLogRepository loginLogRepository;

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/staff-login")
    public String staffLoginPage() {
        return "forward:/staff-login.html";
    }

    @GetMapping("/student-login")
    public String studentLoginPage() {
        return "forward:/student-login.html";
    }

    // Legacy URL kept working: registration now lives inside the login pages as a tab
    @GetMapping("/register")
    public String registerPage() {
        return "redirect:/staff-login?tab=register";
    }

    @PostMapping("/staff-login")
    public String processStaffLogin(@RequestParam String username,
                                    @RequestParam String password,
                                    HttpSession session,
                                    HttpServletRequest request) {
        try {
            Staff staff = staffRepository.findByUsername(username).orElse(null);

            // No account with this username -> stay on Login tab and say so clearly
            if (staff == null) {
                return "redirect:/staff-login?error=notfound";
            }

            if (PasswordUtil.matches(password, staff.getPassword())) {
                session.setAttribute("staffUsername", username);
                session.setAttribute("staffName", staff.getName());
                session.setAttribute("role", "staff");

                LoginLog log = new LoginLog();
                log.setUsername(username);
                log.setRole("staff");
                log.setLoginTime(LocalDateTime.now());
                log.setIpAddress(request.getRemoteAddr());
                loginLogRepository.save(log);

                return "redirect:/dashboard";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/staff-login?error=system";
        }
        return "redirect:/staff-login?error=wrongpassword";
    }

    @PostMapping("/student-login")
    public String processStudentLogin(@RequestParam String username,
                                      @RequestParam String password,
                                      HttpSession session,
                                      HttpServletRequest request) {
        try {
            Student student = studentRepository.findByUsername(username).orElse(null);

            // No account with this username -> stay on Login tab and say so clearly
            if (student == null) {
                return "redirect:/student-login?error=notfound";
            }

            if (PasswordUtil.matches(password, student.getPassword())) {
                session.setAttribute("studentUsername", username);
                session.setAttribute("studentName", student.getName());
                session.setAttribute("role", "student");

                LoginLog log = new LoginLog();
                log.setUsername(username);
                log.setRole("student");
                log.setLoginTime(LocalDateTime.now());
                log.setIpAddress(request.getRemoteAddr());
                loginLogRepository.save(log);

                return "redirect:/student-dashboard";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/student-login?error=system";
        }
        return "redirect:/student-login?error=wrongpassword";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/register-staff")
    public String registerStaff(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam String department) {
        try {
            if (staffRepository.findByUsername(username).isPresent()) {
                return "redirect:/staff-login?error=already_registered";
            }

            Staff staff = new Staff();
            staff.setUsername(username);
            staff.setPassword(PasswordUtil.encode(password));
            staff.setName(name);
            staff.setEmail(email);
            staff.setPhone(phone);
            staff.setDepartment(department);
            staffRepository.save(staff);

            return "redirect:/staff-login?registered=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/staff-login?tab=register&error=system";
        }
    }

    @PostMapping("/register-student")
    public String registerStudent(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam String department,
                                  @RequestParam String year,
                                  @RequestParam String section) {
        try {
            if (studentRepository.findByUsername(username).isPresent()) {
                return "redirect:/student-login?error=already_registered";
            }

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

            return "redirect:/student-login?registered=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/student-login?tab=register&error=system";
        }
    }
}
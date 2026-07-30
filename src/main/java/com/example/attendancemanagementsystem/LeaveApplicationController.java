package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class LeaveApplicationController {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    @GetMapping("/apply-leave")
    public String applyLeavePage(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return "redirect:/student-login";
        }
        return "forward:/apply-leave.html";
    }

    @GetMapping("/leave-management")
    public String leaveManagementPage(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }
        return "forward:/leave-management.html";
    }

    // ---------- Student actions ----------

    @PostMapping("/submit-leave")
    public String submitLeave(@RequestParam String fromDate,
                              @RequestParam String toDate,
                              @RequestParam String reason,
                              HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return "redirect:/student-login";
        }

        LeaveApplication leave = new LeaveApplication();
        leave.setStudentUsername((String) session.getAttribute("studentUsername"));
        leave.setStudentName((String) session.getAttribute("studentName"));
        leave.setFromDate(LocalDate.parse(fromDate));
        leave.setToDate(LocalDate.parse(toDate));
        leave.setReason(reason);
        leave.setStatus("Pending");
        leave.setAppliedDate(LocalDate.now());
        leaveApplicationRepository.save(leave);

        return "redirect:/apply-leave";
    }

    @GetMapping("/api/leave/my")
    @ResponseBody
    public List<LeaveApplication> myLeaves(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("student")) {
            return List.of();
        }
        String username = (String) session.getAttribute("studentUsername");
        return leaveApplicationRepository.findByStudentUsername(username);
    }

    // ---------- Staff actions ----------

    @GetMapping("/api/leave/all")
    @ResponseBody
    public List<LeaveApplication> allLeaves(HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return List.of();
        }
        return leaveApplicationRepository.findAllByOrderByAppliedDateDesc();
    }

    @PostMapping("/api/leave/{id}/approve")
    @ResponseBody
    public String approveLeave(@PathVariable Long id, HttpSession session) {
        return respondToLeave(id, "Approved", session);
    }

    @PostMapping("/api/leave/{id}/reject")
    @ResponseBody
    public String rejectLeave(@PathVariable Long id, HttpSession session) {
        return respondToLeave(id, "Rejected", session);
    }

    private String respondToLeave(Long id, String status, HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "error";
        }

        LeaveApplication leave = leaveApplicationRepository.findById(id).orElse(null);
        if (leave == null) {
            return "not-found";
        }

        leave.setStatus(status);
        leave.setStaffResponse((String) session.getAttribute("staffName") + " " + status.toLowerCase() + " this request.");
        leaveApplicationRepository.save(leave);

        Notification notification = new Notification();
        notification.setUsername(leave.getStudentUsername());
        notification.setRole("student");
        notification.setMessage("Your leave application (" + leave.getFromDate() + " to " + leave.getToDate() + ") was " + status.toLowerCase() + ".");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return "ok";
    }
}
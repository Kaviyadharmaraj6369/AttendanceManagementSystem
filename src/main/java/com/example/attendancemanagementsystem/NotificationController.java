package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public String notificationsPage(HttpSession session) {
        if (session.getAttribute("role") == null) {
            return "redirect:/";
        }
        return "forward:/notifications.html";
    }

    @GetMapping("/api/notifications")
    @ResponseBody
    public List<Notification> myNotifications(HttpSession session) {
        String[] identity = currentIdentity(session);
        if (identity == null) {
            return List.of();
        }
        return notificationRepository.findByUsernameAndRoleOrderByCreatedAtDesc(identity[0], identity[1]);
    }

    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public long unreadCount(HttpSession session) {
        String[] identity = currentIdentity(session);
        if (identity == null) {
            return 0;
        }
        return notificationRepository.countByUsernameAndRoleAndIsReadFalse(identity[0], identity[1]);
    }

    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public String markRead(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("role") == null) {
            return "error";
        }
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            return "not-found";
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        return "ok";
    }

    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public String markAllRead(HttpSession session) {
        String[] identity = currentIdentity(session);
        if (identity == null) {
            return "error";
        }
        List<Notification> notifications = notificationRepository.findByUsernameAndRoleOrderByCreatedAtDesc(identity[0], identity[1]);
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
        return "ok";
    }

    private String[] currentIdentity(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null) {
            return null;
        }
        String username = "staff".equals(role)
                ? (String) session.getAttribute("staffUsername")
                : (String) session.getAttribute("studentUsername");
        if (username == null) {
            return null;
        }
        return new String[]{username, role};
    }
}
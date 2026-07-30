package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/announcements")
    public String announcementsPage(HttpSession session) {
        if (session.getAttribute("role") == null) {
            return "redirect:/";
        }
        return "forward:/announcements.html";
    }

    @GetMapping("/api/announcements")
    @ResponseBody
    public List<Announcement> allAnnouncements(HttpSession session) {
        if (session.getAttribute("role") == null) {
            return List.of();
        }
        return announcementRepository.findAllByOrderByPostedDateDesc();
    }

    @PostMapping("/add-announcement")
    public String addAnnouncement(@RequestParam String title,
                                  @RequestParam String message,
                                  @RequestParam String priority,
                                  HttpSession session) {
        if (session.getAttribute("role") == null || !session.getAttribute("role").equals("staff")) {
            return "redirect:/staff-login";
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setPriority(priority);
        announcement.setPostedBy((String) session.getAttribute("staffName"));
        announcement.setPostedDate(LocalDateTime.now());
        announcementRepository.save(announcement);


        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            Notification notification = new Notification();
            notification.setUsername(student.getUsername());
            notification.setRole("student");
            notification.setMessage("New announcement: " + title);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return "redirect:/announcements";
    }
}
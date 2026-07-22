package com.example.attendancemanagementsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LoginLogController {

    @Autowired
    private LoginLogRepository loginLogRepository;

    @GetMapping("/login-logs")
    public List<LoginLog> getLoginLogs() {
        return loginLogRepository.findAllByOrderByLoginTimeDesc();
    }
}
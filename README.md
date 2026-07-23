# 📚 Attendance Management System

## 📌 Project Overview

The Attendance Management System is a full-stack web application developed using **Spring Boot**, **MySQL**, **HTML**, **CSS**, and **JavaScript**. It helps educational institutions automate attendance management by reducing manual work and providing an efficient platform for both students and staff.

Students can view their attendance, apply for leave, receive announcements and notifications, while staff can manage student records, mark attendance, review leave requests, and generate attendance reports.

---

## 🎯 Objective

The main objective of this project is to automate attendance management, improve accuracy, reduce paperwork, and provide an easy-to-use system for students and staff.

---

## 🚀 Features

### 👨‍🎓 Student Module

- Student Login
- View Attendance
- View Attendance Reports
- Apply for Leave
- View Notifications
- View Announcements
- View Student Profile

### 👨‍🏫 Staff Module

- Staff Login
- Manage Student Records
- Mark Student Attendance
- View Attendance Reports
- Approve or Reject Leave Applications
- Post Announcements
- Send Notifications
- View Login Logs

---

## 🛠️ Technologies Used

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Maven

### Frontend

- HTML5
- CSS3
- JavaScript

### Database

- MySQL

### Tools

- IntelliJ IDEA
- MySQL Workbench
- Postman
- Git
- GitHub

---

## 📂 Project Structure

```
AttendanceManagementSystem
│── src
│   ├── main
│   │   ├── java
│   │   │   ├── controller
│   │   │   ├── repository
│   │   │   ├── model
│   │   │   └── AttendanceManagementSystemApplication.java
│   │   └── resources
│   │       ├── static
│   │       └── application.properties
│── pom.xml
│── README.md
```

---

## 📸 Application Screens

- Home Page
- Student Login
- Staff Login
- Student Dashboard
- Staff Dashboard
- Attendance Management
- Student Management
- Leave Management
- Notifications
- Reports

> *(Screenshots can be added here.)*

---

## ⚙️ Installation Guide

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/Kaviyadharmaraj6369/AttendanceManagementSystem.git
```

### 2️⃣ Open the Project

Open the project using **IntelliJ IDEA**.

### 3️⃣ Configure MySQL

Create a database.

```sql
CREATE DATABASE attendance_db;
```

Update the database details in:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4️⃣ Run the Application

Run:

```
AttendanceManagementSystemApplication.java
```

### 5️⃣ Open the Application

```
http://localhost:8080
```

---

## 🌟 Future Enhancements

- QR Code Based Attendance
- Face Recognition Attendance
- Email Notifications
- Attendance Analytics Dashboard
- Mobile Application
- Parent Portal
- Export Reports (PDF & Excel)

---

## 👩‍💻 Author

**Kaviya D**

- GitHub: https://github.com/Kaviyadharmaraj6369

---

## ⭐ Support

If you found this project useful, please consider giving this repository a ⭐ on GitHub.

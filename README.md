# Secure Online Exam System

A full-stack exam management system built in Java, allowing teachers to create exams and students to attempt them with automatic grading — built to strengthen backend, database, and security fundamentals.

## Features
- Role-based authentication (Teacher / Student) with SHA-256 password hashing
- Teachers can create exams and add multiple-choice questions
- Students can view exam questions and submit answers
- Automatic grading with results stored in the database
- SQL injection protection using PreparedStatements throughout

## Tech Stack
- **Language:** Java (JDBC)
- **Database:** MySQL
- **Tools:** MySQL Workbench, VS Code, Git/GitHub

## Database Schema
- `Users` — stores teacher/student accounts with hashed passwords and role
- `Exams` — exam title, duration, and creator
- `Questions` — MCQ questions linked to an exam, with 4 options and the correct answer
- `Results` — student scores per exam attempt

## How to Run
1. Set up a MySQL database named `exam_system` and run the schema (see `schema.sql`)
2. Update the database password in `DBConnection.java`
3. Compile: `javac *.java`
4. Run any module, e.g.: `java -cp ".;lib/mysql-connector-j-26.7.0.jar" AuthService`

## What This Project Demonstrates
- JDBC database connectivity and CRUD operations
- Secure password storage practices
- Role-based access logic
- Clean separation of concerns (DBConnection, AuthService, ExamService, StudentService)

## Author
Harshita Jain — BCA (Cyber Security), JECRC University
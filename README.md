# ⚙️ AI Job Portal — Backend

> A production-ready Spring Boot REST API powering an AI-driven job portal. Features JWT authentication, Gemini AI resume scoring, automated email notifications via Gmail SMTP, and MySQL database.

🌐 **Live API:** [https://ai-job-portal-backend-ng39.onrender.com](https://ai-job-portal-backend-ng39.onrender.com)  
🖥️ **Frontend:** [https://ai-job-portal-frontend-fmkc.vercel.app](https://ai-job-portal-frontend-fmkc.vercel.app)  
📦 **Frontend Repo:** [https://github.com/2003chinmayee/AI-Job-Portal-Frontend](https://github.com/2003chinmayee/AI-Job-Portal-Frontend)

---

## 📌 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [How It Works — Flow](#how-it-works--flow)
- [AI Integration](#ai-integration)
- [Email System](#email-system)
- [Security & Authentication](#security--authentication)
- [Hosting & Deployment](#hosting--deployment)
- [Environment Variables](#environment-variables)
- [How to Run Locally](#how-to-run-locally)
- [Interview Q&A](#interview-qa)

---

## 📖 Project Overview

This is the backend for the AI Job Portal — a RESTful API built with **Spring Boot 3** and **Java 21**. It handles user authentication, job management, application processing, AI-powered resume scoring using **Google Gemini AI**, automated email notifications via **Gmail SMTP**, and a real-time notification system.

This project was built as a **Final Year MBE Project** and also serves as a **Portfolio Project**.

---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based stateless authentication
- BCrypt password encryption
- Role-based access control (CANDIDATE / RECRUITER)
- Duplicate email and phone number prevention
- Password strength enforcement
- CORS configuration for cross-origin requests

### 💼 Job Management
- Recruiters can post, update, and manage job listings
- Active/History job separation
- Job analytics (applicant count, shortlisted, hired, rejected)
- Closing date management

### 📋 Application System
- Multi-field application with PDF resume upload
- PDF text extraction using **Apache PDFBox**
- Duplicate application prevention
- Application status tracking (APPLIED → SHORTLISTED → HIRED/REJECTED)
- Candidate application history with 30-day archiving

### 🤖 AI Resume Scoring
- Gemini AI analyzes resume text against job requirements
- Returns AI match score (0-100)
- Identifies matched skills and missing skills
- Provides strengths, recommendations, and feedback
- Scores calculated on application submit AND on recruiter AI analysis

### 📧 Email Notification System
- Gmail SMTP integration
- Beautiful HTML email templates for:
  - Shortlist notification with interview details
  - Hire notification with official offer letter
  - Rejection notification with encouragement
- Reply-to set to recruiter's email

### 🔔 In-App Notification System
- Notifications created on application submit and status change
- Unread count endpoint for bell badge
- Mark all as read / mark one as read
- Notification types: APPLICATION_SUBMITTED, STATUS_UPDATE

### 🎓 Master Data Management
- Education, College, University, Skills master tables
- Candidates can add custom entries not in the list
- Data seeded automatically on first run

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming language |
| **Spring Boot** | 3.2.0 | Backend framework |
| **Spring Security** | 6.2.0 | Authentication & authorization |
| **Spring Data JPA** | 3.2.0 | Database ORM |
| **Hibernate** | 6.3.1 | JPA implementation |
| **MySQL** | 8.x | Relational database |
| **JWT (jjwt)** | 0.11.5 | Token-based authentication |
| **BCrypt** | - | Password hashing |
| **Apache PDFBox** | 3.0.2 | PDF text extraction |
| **Google Gemini AI** | via REST API | Resume scoring & analysis |
| **JavaMailSender** | - | Gmail SMTP email sending |
| **Lombok** | 1.18.32 | Boilerplate code reduction |
| **Maven** | 3.9.5 | Build tool |
| **Docker** | - | Containerization for deployment |
| **Render** | - | Backend hosting |
| **Railway** | - | MySQL database hosting |

---

## 🏗️ Project Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (React Frontend)                    │
│              https://ai-job-portal-frontend-fmkc.vercel.app  │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTPS REST API calls
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot Backend (Render)                     │
│         https://ai-job-portal-backend-ng39.onrender.com      │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  Controllers │  │   Services   │  │   Repositories   │  │
│  │  (REST API)  │→ │ (Business    │→ │  (JPA/Database)  │  │
│  │              │  │   Logic)     │  │                  │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
│                           │                                  │
│              ┌────────────┼────────────┐                    │
│              ▼            ▼            ▼                    │
│        Gemini AI    Gmail SMTP    JWT Security              │
└─────────────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                MySQL Database (Railway)                       │
│                    jobportal database                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
job-portal-backend/
├── src/main/java/com/jobportal/backend/
│   ├── config/
│   │   ├── JwtFilter.java           # JWT token validation filter
│   │   ├── JwtUtil.java             # JWT token generation & validation
│   │   └── SecurityConfig.java      # Spring Security configuration
│   ├── controller/
│   │   ├── AuthController.java      # Register & Login endpoints
│   │   ├── JobController.java       # Job CRUD endpoints
│   │   ├── ApplicationController.java # Application endpoints
│   │   ├── RecruiterController.java # Shortlist/Hire/Reject endpoints
│   │   ├── NotificationController.java # Notification endpoints
│   │   ├── UserProfileController.java # Profile management
│   │   ├── CollegeMasterController.java # College master data
│   │   ├── EducationMasterController.java # Education master data
│   │   ├── SkillMasterController.java # Skills master data
│   │   ├── UniversityMasterController.java # University master data
│   │   └── GeminiController.java    # Direct AI analysis endpoint
│   ├── dto/
│   │   ├── InterviewDetailsDTO.java # Interview scheduling data
│   │   ├── JobAnalyticsDTO.java     # Analytics data transfer
│   │   └── RegisterRequest.java    # Registration request body
│   ├── model/
│   │   ├── User.java               # User entity
│   │   ├── Job.java                # Job listing entity
│   │   ├── Application.java        # Job application entity
│   │   ├── Resume.java             # Resume text storage entity
│   │   ├── Notification.java       # Notification entity
│   │   ├── CollegeMaster.java      # College master entity
│   │   ├── EducationMaster.java    # Education master entity
│   │   ├── SkillMaster.java        # Skills master entity
│   │   └── UniversityMaster.java  # University master entity
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── JobRepository.java
│   │   ├── ApplicationRepository.java
│   │   ├── ResumeRepository.java
│   │   ├── NotificationRepository.java
│   │   ├── CollegeMasterRepository.java
│   │   ├── EducationMasterRepository.java
│   │   ├── SkillMasterRepository.java
│   │   └── UniversityMasterRepository.java
│   ├── service/
│   │   ├── UserService.java         # User registration & login logic
│   │   ├── JobService.java          # Job management logic
│   │   ├── ApplicationService.java  # Application processing & AI scoring
│   │   ├── AiRankingService.java    # Gemini AI integration & ranking
│   │   ├── EmailService.java        # Gmail SMTP email sending
│   │   ├── NotificationService.java # Notification creation & management
│   │   └── ResumeService.java       # Resume analysis logic
│   └── JobPortalBackendApplication.java # Main Spring Boot entry point
├── src/main/resources/
│   ├── application.properties       # Main config (uses env variables)
│   └── application-local.properties # Local dev config (gitignored)
├── Dockerfile                       # Docker configuration for deployment
└── pom.xml                         # Maven dependencies
```

---

## 🗄️ Database Schema

### users
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT PK | Auto-generated ID |
| name | VARCHAR | Full name |
| email | VARCHAR UNIQUE | Email address |
| password | VARCHAR | BCrypt hashed password |
| role | VARCHAR | CANDIDATE or RECRUITER |
| phone | VARCHAR UNIQUE | 10-digit phone |
| location | VARCHAR | City |
| bio | TEXT | Profile bio |
| skills | TEXT | Comma-separated skills |
| education | VARCHAR | Education level |
| experience | TEXT | Work experience |
| resume_url | VARCHAR | Resume file URL |

### jobs
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT PK | Auto-generated ID |
| title | VARCHAR | Job title |
| company | VARCHAR | Company name |
| location | VARCHAR | Job location |
| salary | VARCHAR | Salary range |
| experience | VARCHAR | Required experience |
| description | TEXT | Job description |
| requirements | TEXT | Skills required |
| job_type | VARCHAR | Full-time/Part-time |
| vacancies | INT | Number of openings |
| posted_by | VARCHAR | Recruiter email |
| posted_at | DATETIME | Post timestamp |
| closing_date | DATE | Application deadline |
| active | BOOLEAN | Is job active |

### applications
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT PK | Auto-generated ID |
| candidate_id | BIGINT FK | Reference to user |
| job_id | BIGINT FK | Reference to job |
| candidate_name | VARCHAR | Applicant name |
| candidate_email | VARCHAR | Applicant email |
| phone | VARCHAR | Contact number |
| education | VARCHAR | Education level |
| college_name | VARCHAR | College name |
| university_name | VARCHAR | University name |
| percentage | VARCHAR | CGPA/Percentage |
| gender | VARCHAR | Gender |
| date_of_birth | VARCHAR | DOB |
| year_of_passing | VARCHAR | Graduation year |
| cover_letter | TEXT | Cover letter text |
| resume_file_name | VARCHAR | Uploaded PDF name |
| skills | TEXT | Comma-separated skills |
| status | VARCHAR | APPLIED/SHORTLISTED/HIRED/REJECTED |
| ai_score | DOUBLE | Gemini AI match score |
| skills_match | TEXT | Matched skills |
| missing_skills | TEXT | Missing skills |
| strengths | TEXT | Candidate strengths |
| ai_recommendation | TEXT | AI recommendation |
| ai_feedback | TEXT | Detailed AI feedback |
| job_title | VARCHAR | Denormalized job title |
| company | VARCHAR | Denormalized company |
| applied_at | DATETIME | Application timestamp |
| contact_email | VARCHAR | Recruiter contact email |
| contact_person | VARCHAR | Recruiter contact name |
| interview_date | VARCHAR | Interview date |
| interview_time | VARCHAR | Interview time |
| interview_mode | VARCHAR | Online/Offline |
| meeting_link | VARCHAR | Video call link |
| joining_date | VARCHAR | Offer joining date |
| salary | VARCHAR | Offered salary |
| office_location | VARCHAR | Office location |

### notifications
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT PK | Auto-generated ID |
| user_id | BIGINT FK | Reference to user |
| title | VARCHAR | Notification title |
| message | TEXT | Notification body |
| type | VARCHAR | APPLICATION_SUBMITTED/STATUS_UPDATE |
| is_read | BOOLEAN | Read status |
| created_at | DATETIME | Creation timestamp |

### resumes
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT PK | Auto-generated ID |
| user_id | BIGINT | Candidate ID |
| file_name | VARCHAR | PDF filename |
| file_path | VARCHAR | Storage path |
| extracted_text | TEXT | PDF text content (max 5000 chars) |

---

## 🔗 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |
| GET | `/api/auth/test` | API health check | No |

### 💼 Jobs
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/jobs` | Get all active jobs | No |
| GET | `/api/jobs/{id}` | Get job by ID | No |
| POST | `/api/jobs` | Create new job | Yes (RECRUITER) |
| PUT | `/api/jobs/{id}` | Update job | Yes (RECRUITER) |
| DELETE | `/api/jobs/{id}` | Delete job | Yes (RECRUITER) |
| GET | `/api/jobs/recruiter/active` | Get recruiter's active jobs | Yes |
| GET | `/api/jobs/recruiter/history` | Get recruiter's past jobs | Yes |

### 📋 Applications
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/applications/apply` | Apply for job (multipart) | Yes |
| GET | `/api/applications/candidate/{id}` | Get candidate's applications | Yes |
| GET | `/api/applications/candidate/{id}/active` | Get active applications | Yes |
| GET | `/api/applications/candidate/{id}/history` | Get application history | Yes |
| GET | `/api/applications/candidate/{id}/summary` | Get stats summary | Yes |
| GET | `/api/applications/job/{jobId}` | Get job's applicants | Yes |
| PUT | `/api/applications/{id}/status` | Update status | Yes |

### 👩‍💼 Recruiter Actions
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| PUT | `/api/recruiter/shortlist/{id}` | Shortlist + send email | Yes |
| PUT | `/api/recruiter/hire/{id}` | Hire + send offer email | Yes |
| PUT | `/api/recruiter/reject/{id}` | Reject + send email | Yes |
| GET | `/api/recruiter/ai-rank/{jobId}` | Get AI ranked candidates | Yes |
| POST | `/api/recruiter/analyze/{jobId}` | Run AI analysis | Yes |
| GET | `/api/recruiter/analytics` | Get job analytics | Yes |

### 🔔 Notifications
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/notifications` | Get all notifications | Yes |
| GET | `/api/notifications/unread-count` | Get unread count | Yes |
| PUT | `/api/notifications/mark-all-read` | Mark all as read | Yes |
| PUT | `/api/notifications/{id}/read` | Mark one as read | Yes |

### 👤 Profile
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/profile` | Get user profile | Yes |
| PUT | `/api/profile` | Update profile | Yes |

---

## 🔄 How It Works — Flow

### Application Submit Flow:
```
1. Candidate submits multi-step form with PDF resume
2. Backend receives multipart request with all fields
3. Apache PDFBox extracts text from PDF (up to 5000 chars)
4. Extracted text saved to resumes table
5. Gemini AI analyzes resume text against job requirements
6. AI returns: score (0-100), matched skills, missing skills, feedback
7. Application saved to database with AI score
8. Notification created for candidate
9. Response returned to frontend with AI results
```

### Recruiter Shortlist Flow:
```
1. Recruiter clicks Shortlist button on Applicants page
2. Frontend sends PUT to /api/recruiter/shortlist/{id} with interview details
3. Backend updates application status to SHORTLISTED
4. EmailService sends HTML shortlist email via Gmail SMTP
5. NotificationService creates in-app notification
6. Candidate receives email + bell notification
```

### AI Ranking Flow:
```
1. Recruiter clicks "Run AI Analysis"
2. Backend fetches all applications for the job
3. For each application without a score:
   a. Fetches resume text from resumes table
   b. Sends to Gemini AI with job requirements
   c. Parses AI response for score + details
   d. Updates application with AI data
4. Returns candidates sorted by AI score (highest first)
```

---

## 🤖 AI Integration

### Gemini AI (Google)
- **Model:** `gemini-2.5-flash-lite` (upgraded from gemini-pro)
- **API:** REST HTTP call to Google Generative Language API
- **Location:** `AiRankingService.java`

### How the prompt works:
```
The AI receives:
1. Resume text (extracted from PDF)
2. Job title, description, requirements
3. Candidate's listed skills, education, experience

The AI returns:
- Score: 0-100 match percentage
- Skills Match: comma-separated matched skills
- Missing Skills: skills the candidate lacks
- Strengths: candidate's strong points
- Recommendation: hire/consider/reject
- Experience Match: yes/no/partial
- Education Match: yes/no
- Feedback: detailed improvement suggestions
```

### Response parsing:
The AI response is parsed line by line using regex patterns:
```java
// Example parsing:
if (line.startsWith("Score:")) {
    score = Double.parseDouble(line.replace("Score:", "").trim());
}
```

---

## 📧 Email System

### Gmail SMTP Configuration
- **Host:** smtp.gmail.com
- **Port:** 587
- **Security:** STARTTLS
- **Auth:** Gmail App Password (not regular password)

### Email Templates
All emails are sent as **HTML** with beautiful styling:

1. **Shortlist Email** — Purple gradient header, interview details table, recruiter contact
2. **Hire Email** — Green gradient header, official offer letter with salary/joining date
3. **Rejection Email** — Grey gradient header, encouraging message

### How to generate Gmail App Password:
1. Go to myaccount.google.com
2. Security → 2-Step Verification (enable)
3. Search "App Passwords"
4. Generate → Copy 16-character password

---

## 🔐 Security & Authentication

### JWT (JSON Web Token)
- **Library:** jjwt 0.11.5
- **Secret:** Fixed secret key in JwtUtil.java (prevents logout on restart)
- **Expiry:** Configurable (default 24 hours)
- **Filter:** JwtFilter.java intercepts every request

### Spring Security Configuration:
```java
// Public endpoints (no JWT needed):
/api/auth/**     → Login, Register
/api/jobs/**     → Browse jobs
/api/applications/** → Apply

// Protected endpoints (JWT required):
/api/notifications/** → User-specific
/api/profile/**      → User-specific
```

### Password Security:
- BCrypt hashing with salt rounds
- Plain text password never stored
- Validation: min 8 chars, uppercase, lowercase, number, special character

### CORS Configuration:
Allows requests from:
- `http://localhost:3000` (local development)
- `http://localhost:3001` (alternate local)
- `https://ai-job-portal-frontend-fmkc.vercel.app` (production)

---

## 🌐 Hosting & Deployment

| Service | Platform | Details |
|---------|----------|---------|
| Backend API | **Render** (free) | Docker container, auto-deploy on push |
| Database | **Railway** (free $5 credit) | Managed MySQL, public URL |
| Frontend | **Vercel** (free) | Auto-deploy on push |

### Dockerfile:
```dockerfile
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Auto-Deployment:
Push to `main` branch → Render detects change → Rebuilds Docker image → Deploys automatically

---

## 🔧 Environment Variables

```properties
# Database (Railway MySQL)
SPRING_DATASOURCE_URL=jdbc:mysql://reseau.proxy.rlwy.net:18718/railway
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# AI
GEMINI_API_KEY=your_gemini_api_key

# Email
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_16_char_app_password

# Profile
SPRING_PROFILES_ACTIVE=prod
```

---

## 💻 How to Run Locally

```bash
# Clone the repository
git clone https://github.com/2003chinmayee/AI-Job-Portal-Backend.git
cd AI-Job-Portal-Backend

# Create local properties file
# src/main/resources/application-local.properties
GEMINI_API_KEY=your_key
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Make sure MySQL is running locally
# Create database: CREATE DATABASE jobportal;

# Run with Maven
./mvnw spring-boot:run

# Or build and run jar
./mvnw clean package -DskipTests
java -jar target/*.jar

# API runs on http://localhost:8080
```

---

## 🎤 Interview Q&A

**Q: What is this project?**
> A full-stack AI-powered job portal. The backend is a Spring Boot 3 REST API with JWT authentication, Gemini AI resume scoring, Gmail SMTP email notifications, and MySQL database. Deployed using Docker on Render with Railway MySQL.

**Q: Why did you choose Spring Boot?**
> Spring Boot provides auto-configuration, dependency injection, and a production-ready embedded Tomcat server. Spring Security makes JWT authentication straightforward, and Spring Data JPA reduces boilerplate database code significantly.

**Q: How does JWT authentication work?**
> On login, the server generates a JWT token signed with a secret key containing the user's email. The token is returned to the client and stored in localStorage. Every subsequent request includes the token in the Authorization header. JwtFilter.java intercepts every request, validates the token, and sets the Security Context.

**Q: How does the AI resume scoring work?**
> When a candidate submits an application, Apache PDFBox extracts text from the uploaded PDF. This text is sent to Google Gemini AI along with the job title, description, and requirements in a structured prompt. Gemini returns a score from 0-100, matched skills, missing skills, strengths, and feedback. This is parsed and stored with the application.

**Q: How did you integrate Gemini AI?**
> Through the Google Generative Language REST API. I make an HTTP POST request with the model name, prompt, and API key. The response is parsed by extracting specific fields like Score, SkillsMatch, MissingSkills from the text response using string parsing.

**Q: How does the email system work?**
> Using Spring's JavaMailSender with Gmail SMTP on port 587 with STARTTLS. When a recruiter shortlists/hires/rejects a candidate, the EmailService builds an HTML email template with the relevant details and sends it via Gmail. A Gmail App Password is used instead of the regular account password for security.

**Q: How does the notification system work?**
> Notifications are stored in a notifications table linked to users. They're created when a candidate applies for a job and when their status changes. The frontend polls `/notifications/unread-count` on every route change to update the bell badge. When the user visits the notifications page, all are marked as read.

**Q: What is BCrypt and why did you use it?**
> BCrypt is a password hashing algorithm with built-in salt. It's computationally expensive by design, making brute-force attacks difficult. Spring Security's BCryptPasswordEncoder handles hashing on registration and verification on login. Plain text passwords are never stored.

**Q: What is Apache PDFBox?**
> An open-source Java library for working with PDF files. I used it to extract raw text content from uploaded PDF resumes so the text can be sent to Gemini AI for analysis. The PDFTextStripper class reads all text from all pages.

**Q: How did you handle CORS?**
> CORS is configured in SecurityConfig.java using Spring Security's CORS filter. Allowed origins include localhost ports for development and the production Vercel URL. Allowed methods include GET, POST, PUT, DELETE, and OPTIONS (for preflight requests).

**Q: Why did you use Docker for deployment?**
> Render doesn't have a native Java runtime option. Docker ensures the application runs in a consistent, isolated environment regardless of the host. The multi-stage Dockerfile first builds the JAR with Maven, then creates a minimal runtime image with only the JRE, keeping the image small.

**Q: How do you prevent duplicate applications?**
> The ApplicationRepository has an `existsByCandidateIdAndJobId()` method. Before saving a new application, the service checks if the same candidate has already applied for the same job. If yes, it throws a RuntimeException with a user-friendly message.

**Q: What design patterns did you use?**
> Repository pattern (Spring Data JPA repositories), Service layer pattern (business logic separated from controllers), DTO pattern (data transfer objects for request/response), Filter pattern (JwtFilter for authentication), and Builder pattern (Gemini API request building).

**Q: What was the most challenging part?**
> Integrating all three external services (Gemini AI, Gmail SMTP, MySQL on Railway) while keeping the code maintainable. Also managing Spring Security configuration — getting `permitAll()` and `authenticated()` rules right for different endpoints took careful debugging. Deploying with Docker on Render and connecting to Railway MySQL with the public URL was also a learning experience.

**Q: How is the project structured (layers)?**
> It follows a standard 3-layer architecture:
> 1. **Controller layer** — Handles HTTP requests/responses, input validation
> 2. **Service layer** — Business logic, AI calls, email sending
> 3. **Repository layer** — Database operations via Spring Data JPA
> This separation makes the code testable, maintainable, and scalable.

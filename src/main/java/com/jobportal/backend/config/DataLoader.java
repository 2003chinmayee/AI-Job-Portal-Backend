package com.jobportal.backend.config;

import com.jobportal.backend.model.Job;
import com.jobportal.backend.model.User;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.jobportal.backend.repository.EducationMasterRepository;
import com.jobportal.backend.model.EducationMaster;
import com.jobportal.backend.model.CollegeMaster;
import com.jobportal.backend.model.SkillMaster;
import com.jobportal.backend.repository.CollegeMasterRepository;
import com.jobportal.backend.repository.SkillMasterRepository;
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EducationMasterRepository educationMasterRepository;

    @Autowired
    private CollegeMasterRepository collegeMasterRepository;

    @Autowired
    private SkillMasterRepository skillMasterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


        if (educationMasterRepository.count() == 0) {

            educationMasterRepository.save(new EducationMaster("Computer Engineering"));
            educationMasterRepository.save(new EducationMaster("Computer Science"));
            educationMasterRepository.save(new EducationMaster("Information Technology"));
            educationMasterRepository.save(new EducationMaster("Mechanical Engineering"));
            educationMasterRepository.save(new EducationMaster("Civil Engineering"));
            educationMasterRepository.save(new EducationMaster("Electrical Engineering"));
            educationMasterRepository.save(new EducationMaster("Electronics Engineering"));
            educationMasterRepository.save(new EducationMaster("MBA"));
            educationMasterRepository.save(new EducationMaster("BCA"));
            educationMasterRepository.save(new EducationMaster("MCA"));
            educationMasterRepository.save(new EducationMaster("BSc Computer Science"));
            educationMasterRepository.save(new EducationMaster("BCom"));
            educationMasterRepository.save(new EducationMaster("MCom"));
            educationMasterRepository.save(new EducationMaster("Doctor"));
            educationMasterRepository.save(new EducationMaster("Pharmacist"));
            educationMasterRepository.save(new EducationMaster("Other"));

            System.out.println("✅ Education Master Data Loaded!");
        }
        if (collegeMasterRepository.count() == 0) {

            collegeMasterRepository.save(new CollegeMaster("COEP"));
            collegeMasterRepository.save(new CollegeMaster("PCCOE"));
            collegeMasterRepository.save(new CollegeMaster("VIT Pune"));
            collegeMasterRepository.save(new CollegeMaster("MIT-WPU"));
            collegeMasterRepository.save(new CollegeMaster("DY Patil Akurdi"));
            collegeMasterRepository.save(new CollegeMaster("Ajinkya DY Patil School of Engineering"));
            collegeMasterRepository.save(new CollegeMaster("Sinhgad College of Engineering"));

            System.out.println("✅ College Master Data Loaded!");
        }
        if (skillMasterRepository.count() == 0) {

            skillMasterRepository.save(new SkillMaster("Java"));
            skillMasterRepository.save(new SkillMaster("Spring Boot"));
            skillMasterRepository.save(new SkillMaster("React"));
            skillMasterRepository.save(new SkillMaster("Node.js"));
            skillMasterRepository.save(new SkillMaster("MySQL"));
            skillMasterRepository.save(new SkillMaster("MongoDB"));
            skillMasterRepository.save(new SkillMaster("HTML"));
            skillMasterRepository.save(new SkillMaster("CSS"));
            skillMasterRepository.save(new SkillMaster("JavaScript"));
            skillMasterRepository.save(new SkillMaster("Git"));
            skillMasterRepository.save(new SkillMaster("Docker"));
            skillMasterRepository.save(new SkillMaster("AWS"));
            skillMasterRepository.save(new SkillMaster("Python"));
            skillMasterRepository.save(new SkillMaster("Machine Learning"));
            skillMasterRepository.save(new SkillMaster("Artificial Intelligence"));

            System.out.println("✅ Skill Master Data Loaded!");
        }


        // Only load data if database is empty
        if (userRepository.count() == 0) {

            // ===== RECRUITERS =====
            User r1 = new User();
            r1.setName("Rahul Sharma");
            r1.setEmail("recruiter@tcs.com");
            r1.setPassword(passwordEncoder.encode("password123"));
            r1.setRole("RECRUITER");
            r1.setPhone("9876543210");
            r1.setLocation("Pune");
            userRepository.save(r1);

            User r2 = new User();
            r2.setName("Priya Patel");
            r2.setEmail("recruiter@infosys.com");
            r2.setPassword(passwordEncoder.encode("password123"));
            r2.setRole("RECRUITER");
            r2.setPhone("9876543211");
            r2.setLocation("Bangalore");
            userRepository.save(r2);

            // ===== CANDIDATES =====
            User c1 = new User();
            c1.setName("Chinmayee");
            c1.setEmail("chinmayee@gmail.com");
            c1.setPassword(passwordEncoder.encode("123456"));
            c1.setRole("CANDIDATE");
            c1.setPhone("9876543212");
            c1.setLocation("Pune");
            userRepository.save(c1);

            User c2 = new User();
            c2.setName("Amit Kumar");
            c2.setEmail("amit@gmail.com");
            c2.setPassword(passwordEncoder.encode("123456"));
            c2.setRole("CANDIDATE");
            c2.setPhone("9876543213");
            c2.setLocation("Mumbai");
            userRepository.save(c2);

            User c3 = new User();
            c3.setName("Sneha Reddy");
            c3.setEmail("sneha@gmail.com");
            c3.setPassword(passwordEncoder.encode("123456"));
            c3.setRole("CANDIDATE");
            c3.setPhone("9876543214");
            c3.setLocation("Hyderabad");
            userRepository.save(c3);

            // ===== JOBS =====
            Job j1 = new Job();
            j1.setTitle("Java Backend Developer");
            j1.setCompany("TCS");
            j1.setLocation("Pune");
            j1.setDescription("Looking for Java developer with Spring Boot experience.");
            j1.setRequirements("Java, Spring Boot, MySQL, REST APIs");
            j1.setSalary("5-8 LPA");
            j1.setJobType("FULL_TIME");
            j1.setExperience("0-2 years");
            j1.setPostedBy("recruiter@tcs.com");
            jobRepository.save(j1);

            Job j2 = new Job();
            j2.setTitle("React Frontend Developer");
            j2.setCompany("Infosys");
            j2.setLocation("Bangalore");
            j2.setDescription("Looking for React developer to build modern web apps.");
            j2.setRequirements("React, JavaScript, HTML, CSS");
            j2.setSalary("4-7 LPA");
            j2.setJobType("FULL_TIME");
            j2.setExperience("0-2 years");
            j2.setPostedBy("recruiter@infosys.com");
            jobRepository.save(j2);

            Job j3 = new Job();
            j3.setTitle("Full Stack Developer");
            j3.setCompany("Wipro");
            j3.setLocation("Hyderabad");
            j3.setDescription("Full stack developer for end to end web applications.");
            j3.setRequirements("React, Java, Spring Boot, MySQL");
            j3.setSalary("6-10 LPA");
            j3.setJobType("FULL_TIME");
            j3.setExperience("1-3 years");
            j3.setPostedBy("recruiter@wipro.com");
            jobRepository.save(j3);

            Job j4 = new Job();
            j4.setTitle("Data Analyst");
            j4.setCompany("Accenture");
            j4.setLocation("Mumbai");
            j4.setDescription("Analyze data and provide business insights.");
            j4.setRequirements("Python, SQL, Excel, Power BI");
            j4.setSalary("4-6 LPA");
            j4.setJobType("FULL_TIME");
            j4.setExperience("0-1 years");
            j4.setPostedBy("recruiter@accenture.com");
            jobRepository.save(j4);

            Job j5 = new Job();
            j5.setTitle("Software Engineer");
            j5.setCompany("Google");
            j5.setLocation("Bangalore");
            j5.setDescription("Build scalable software for Google products.");
            j5.setRequirements("DSA, Java or Python, System Design");
            j5.setSalary("25-40 LPA");
            j5.setJobType("FULL_TIME");
            j5.setExperience("0-3 years");
            j5.setPostedBy("recruiter@google.com");
            jobRepository.save(j5);

            Job j6 = new Job();
            j6.setTitle("Android Developer");
            j6.setCompany("Flipkart");
            j6.setLocation("Bangalore");
            j6.setDescription("Build Android apps for Flipkart mobile platform.");
            j6.setRequirements("Android, Kotlin, Java, REST APIs");
            j6.setSalary("8-12 LPA");
            j6.setJobType("FULL_TIME");
            j6.setExperience("1-3 years");
            j6.setPostedBy("recruiter@flipkart.com");
            jobRepository.save(j6);

            Job j7 = new Job();
            j7.setTitle("DevOps Engineer");
            j7.setCompany("Amazon");
            j7.setLocation("Hyderabad");
            j7.setDescription("Manage cloud infrastructure and CI/CD pipelines.");
            j7.setRequirements("AWS, Docker, Kubernetes, Jenkins");
            j7.setSalary("12-18 LPA");
            j7.setJobType("FULL_TIME");
            j7.setExperience("2-4 years");
            j7.setPostedBy("recruiter@amazon.com");
            jobRepository.save(j7);

            System.out.println("✅ Sample data loaded!");
            System.out.println("✅ 5 Users added!");
            System.out.println("✅ 7 Jobs added!");

        } else {
            System.out.println("✅ Data already exists — skipping!");
        }
    }
}
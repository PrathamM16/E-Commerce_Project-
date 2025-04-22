package com.triviumbuys;

import com.triviumbuys.entity.Role;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableKafka

@SpringBootApplication
public class TriviumbuysBackendApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(TriviumbuysBackendApplication.class, args);
        System.out.println("Started");
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("Pratham").isEmpty()) {
            User admin = new User();
            admin.setName("Pratham");
            admin.setUsername("Pratham");
            admin.setEmail("pratham@admin.com");
            admin.setPhone("9999999999");
            admin.setPassword(passwordEncoder.encode("Pratham99"));
            admin.setAddress("Admin Headquarters");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created!");
        }
    }
}

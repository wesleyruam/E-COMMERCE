package com.wesleyruan.e_commerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wesleyruan.e_commerce.domain.enums.RolesEnum;
import com.wesleyruan.e_commerce.domain.model.UserModel;
import com.wesleyruan.e_commerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
public class AdminUserConfig implements CommandLineRunner{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var adminRole = RolesEnum.ADMIN;

        var userAdmin = userRepository.findByEmail("admin@e-commerce.com");

        userAdmin.ifPresentOrElse(
            UserModel -> { 
                System.out.println("Admin user already exists."); 
            },
            () -> {
            var newUser = new UserModel();
            newUser.setName("admin");
            newUser.setEmail("admin@e-commerce.com");
            newUser.setPassword(passwordEncoder.encode("admin123"));
            newUser.setRole(adminRole);
            newUser.setPhone("49999378042");
            userRepository.save(newUser);
            System.out.println("Admin user created successfully.");
        });
    }
}

package com.lenceria.sistema_stock.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lenceria.sistema_stock.entities.User;
import com.lenceria.sistema_stock.repositories.UserRepository;

@Configuration
public class DataSeedConfig {

    @Bean
    public CommandLineRunner cargarUsuarioInicial(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Flavio (Dueño - Todo el poder)
            if (userRepository.findByUsername("flavio").isEmpty()) {
                userRepository.save(new User("flavio", passwordEncoder.encode("123456"), "ADMIN"));
            }
            
            // 2. Marisa (Encargada - Todo el poder)
            if (userRepository.findByUsername("marisa").isEmpty()) {
                userRepository.save(new User("marisa", passwordEncoder.encode("123456"), "ADMIN"));
            }
            
            // 3. Cajero (Empleado nuevo - Poder limitado)
            if (userRepository.findByUsername("cajero").isEmpty()) {
                userRepository.save(new User("cajero", passwordEncoder.encode("123"), "VENDEDOR"));
            }
            
            System.out.println("Usuarios base cargados en el sistema.");
        };
    }
}

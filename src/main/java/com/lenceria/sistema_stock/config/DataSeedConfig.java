package com.lenceria.sistema_stock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lenceria.sistema_stock.entities.User;
import com.lenceria.sistema_stock.repositories.UserRepository;

@Configuration
public class DataSeedConfig {

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    public CommandLineRunner cargarUsuarioInicial(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario único ADMIN (consolidado para Flavio y Marisa)
            // La contraseña se obtiene de variable de entorno ADMIN_PASSWORD
            if (userRepository.findByUsername("admin").isEmpty()) {
                if (adminPassword == null || adminPassword.isEmpty()) {
                    throw new IllegalStateException("ERROR: La variable de entorno ADMIN_PASSWORD no está configurada. " +
                            "Por favor configúrala en Render antes de iniciar la aplicación.");
                }
                userRepository.save(new User("admin", passwordEncoder.encode(adminPassword), "ADMIN"));
                System.out.println("✓ Usuario 'admin' creado exitosamente.");
            } else {
                System.out.println("✓ Usuario 'admin' ya existe en el sistema.");
            }

            // Nota: Los usuarios anteriores (flavio, marisa, cajero) han sido eliminados
            // para consolidar en un único usuario administrador seguro.
        };
    }
}

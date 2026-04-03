package com.lenceria.sistema_stock.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.lenceria.sistema_stock.dtos.LoginDTO;
import com.lenceria.sistema_stock.entities.User;
import com.lenceria.sistema_stock.repositories.UserRepository;
import com.lenceria.sistema_stock.security.JwtUtil;

import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // Inyectamos la máquina de pulseras

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());
        
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        User usuarioEncontrado = userOptional.get();

        if(passwordEncoder.matches(loginDTO.getPassword(), usuarioEncontrado.getPassword())){
            
            String token = jwtUtil.generarToken(usuarioEncontrado.getUsername(), usuarioEncontrado.getRol());
            
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("mensaje", "Bienvenido " + usuarioEncontrado.getUsername());
            
            return ResponseEntity.ok(respuesta);
            
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
    }
}

package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.config.JwtService;
import com.upn.hotelreservas.dto.AuthResponse;
import com.upn.hotelreservas.dto.LoginRequest;
import com.upn.hotelreservas.dao.StaffUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y gestión de tokens JWT")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final StaffUserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Iniciar sesión", description = "Recibe credenciales y devuelve un token JWT junto con la información del usuario.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para el usuario: {}", request.getEmail());
        
        // Debug: Verificar si hay usuarios en la base de datos
        long userCount = repository.count();
        log.info("Total de usuarios en la BD: {}", userCount);

        try {
            // DIAGNÓSTICO PROFUNDO
            var userOpt = repository.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                var userDb = userOpt.get();
                log.info("Usuario encontrado en BD. Email: {}", userDb.getEmail());
                log.info("Hash en BD: {}", userDb.getPassword());
                log.info("Password enviado (raw): {}", request.getPassword());
                
                boolean matches = passwordEncoder.matches(request.getPassword(), userDb.getPassword());
                log.info("¿Coinciden los passwords según BCruit?: {}", matches);
                
                if (!matches) {
                    log.warn("LA CONTRASEÑA NO COINCIDE. Generando un hash de prueba para '123456': {}", 
                             passwordEncoder.encode("123456"));
                }
            } else {
                log.warn("Usuario NO encontrado en la BD para el email: {}", request.getEmail());
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            log.info("Autenticación exitosa para: {}", request.getEmail());

            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            var jwtToken = jwtService.generateToken(user);
            log.info("Token generado correctamente.");

            AuthResponse response = AuthResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .token(jwtToken)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en la autenticación para {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas o usuario inactivo");
        }
    }
}

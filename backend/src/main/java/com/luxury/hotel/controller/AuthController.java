package com.luxury.hotel.controller;

import com.luxury.hotel.config.JwtService;
import com.luxury.hotel.dto.AuthResponse;
import com.luxury.hotel.dto.LoginRequest;
import com.luxury.hotel.dao.StaffUserRepository;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y gestión de tokens JWT")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final StaffUserRepository repository;
    private final JwtService jwtService;

    @Operation(summary = "Iniciar sesión", description = "Recibe credenciales y devuelve un token JWT junto con la información del usuario.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para el usuario: {}", request.getEmail());
        
        try {
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

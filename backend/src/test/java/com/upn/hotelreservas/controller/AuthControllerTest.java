package com.upn.hotelreservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upn.hotelreservas.config.JwtService;
import com.upn.hotelreservas.dao.StaffUserRepository;
import com.upn.hotelreservas.dto.LoginRequest;
import com.upn.hotelreservas.model.StaffUser;
import com.upn.hotelreservas.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private StaffUserRepository repository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_Success_ReturnsAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@admin.com");
        request.setPassword("123456");
        
        StaffUser mockUser = new StaffUser();
        mockUser.setId(1L);
        mockUser.setEmail("test@admin.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setFullName("Test Admin");
        mockUser.setRole(UserRole.ADMIN);

        // Simulamos el auth manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // No necesitamos el retorno exacto, solo que no lance excepción
        
        // Simulamos la búsqueda en BD (se llama dos veces en el controller: para logs y para token)
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        
        // Simulamos la verificación de contraseña
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
        
        // Simulamos el generador de token
        when(jwtService.generateToken(mockUser)).thenReturn("fake-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@admin.com"))
                .andExpect(jsonPath("$.fullName").value("Test Admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@admin.com");
        request.setPassword("wrongpassword");

        // Simulamos que el AuthenticationManager lanza excepción de credenciales
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas o usuario inactivo"));
    }

    @Test
    void login_EmptyEmail_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(""); // Email vacío para disparar @NotBlank
        request.setPassword("123456");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_PasswordDoesNotMatch_LogsWarningAndReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@admin.com");
        request.setPassword("wrongpassword");

        StaffUser mockUser = new StaffUser();
        mockUser.setId(1L);
        mockUser.setEmail("test@admin.com");
        mockUser.setPassword("encodedPassword");

        // Simulate user found
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        
        // Simulate password does not match
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);
        // Simulate encode being called inside the !matches branch
        when(passwordEncoder.encode("123456")).thenReturn("hashed123456");

        // Simulate authentication failing
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas o usuario inactivo"));
    }

    @Test
    void login_UserNotFoundAfterAuth_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@admin.com");
        request.setPassword("123456");

        // Simulate auth succeeds
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        
        // Simulate user not found in the DB (for either the first log or the final fetch)
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas o usuario inactivo"));
    }
}

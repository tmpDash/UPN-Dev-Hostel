package com.upn.hotelreservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upn.hotelreservas.model.StaffUser;
import com.upn.hotelreservas.model.UserRole;
import com.upn.hotelreservas.service.StaffUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StaffUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffUserService service;

    @MockBean
    private com.upn.hotelreservas.config.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;
    private StaffUser sampleUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        sampleUser = new StaffUser();
        sampleUser.setId(1L);
        sampleUser.setFullName("Admin Test");
        sampleUser.setEmail("admin@test.com");
        sampleUser.setPassword("password123");
        sampleUser.setRole(UserRole.ADMIN);
        sampleUser.setStatus("ACTIVO");
    }

    @Test
    void listar_ShouldReturnListOfUsers() throws Exception {
        when(service.listarTodos()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@test.com"));
    }

    @Test
    void obtener_WhenExists_ShouldReturnUser() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void obtener_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_ShouldReturnSavedUser() throws Exception {
        when(service.guardar(any(StaffUser.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void crear_InvalidEmail_ReturnsBadRequest() throws Exception {
        sampleUser.setEmail("not-an-email"); // Disparar @Email

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_ShouldReturnUpdatedUser() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleUser));
        when(service.guardar(any(StaffUser.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void actualizar_EmptyFullName_ReturnsBadRequest() throws Exception {
        sampleUser.setFullName(""); // Disparar @NotBlank

        mockMvc.perform(put("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_ShouldReturnNoContent() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

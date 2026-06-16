package com.luxury.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luxury.hotel.model.Hotel;
import com.luxury.hotel.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService service;

    @MockBean
    private com.luxury.hotel.config.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;
    private Hotel sampleHotel;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleHotel = new Hotel();
        sampleHotel.setId(1L);
        sampleHotel.setNombre("Hotel Luxury");
        sampleHotel.setDireccion("Av. La Marina");
        sampleHotel.setCiudad("Lima");
        sampleHotel.setHabitaciones(50);
        sampleHotel.setFechaCreacion(LocalDate.now());
    }

    @Test
    void listar_ShouldReturnListOfHotels() throws Exception {
        when(service.buscarTodos()).thenReturn(Arrays.asList(sampleHotel));

        mockMvc.perform(get("/hoteles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Hotel Luxury"));
    }

    @Test
    void obtener_WhenExists_ShouldReturnHotel() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleHotel));

        mockMvc.perform(get("/hoteles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Hotel Luxury"));
    }

    @Test
    void obtener_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hoteles/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_ShouldReturnSavedHotel() throws Exception {
        when(service.crear(any(Hotel.class))).thenReturn(sampleHotel);

        mockMvc.perform(post("/hoteles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHotel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hotel Luxury"));
    }

    @Test
    void crear_EmptyName_ReturnsBadRequest() throws Exception {
        sampleHotel.setNombre(""); // Disparar @NotBlank

        mockMvc.perform(post("/hoteles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHotel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_ShouldReturnUpdatedHotel() throws Exception {
        when(service.actualizar(eq(1L), any(Hotel.class))).thenReturn(sampleHotel);

        mockMvc.perform(put("/hoteles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHotel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hotel Luxury"));
    }

    @Test
    void actualizar_EmptyCity_ReturnsBadRequest() throws Exception {
        sampleHotel.setCiudad(""); // Disparar @NotBlank

        mockMvc.perform(put("/hoteles/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHotel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_ShouldReturnNoContent() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/hoteles/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

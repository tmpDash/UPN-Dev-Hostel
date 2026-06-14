package com.upn.hotelreservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upn.hotelreservas.model.Reservation;
import com.upn.hotelreservas.model.ReservationStatus;
import com.upn.hotelreservas.model.RoomType;
import com.upn.hotelreservas.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitamos filtros de seguridad temporalmente para pruebas unitarias de controladores
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    @MockBean
    private com.upn.hotelreservas.config.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;
    private Reservation sampleReservation;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleReservation = new Reservation();
        sampleReservation.setId(1L);
        sampleReservation.setCustomerName("Juan Perez");
        sampleReservation.setCheckInDate(LocalDate.now());
        sampleReservation.setCheckOutDate(LocalDate.now().plusDays(3));
        sampleReservation.setRoomType(RoomType.SUITE);
        sampleReservation.setStatus(ReservationStatus.CONFIRMADA);
        sampleReservation.setTotalPrice(450.0);
    }

    @Test
    void listar_ShouldReturnListOfReservations() throws Exception {
        when(service.listarTodas()).thenReturn(Arrays.asList(sampleReservation));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerName").value("Juan Perez"));
    }

    @Test
    void obtener_WhenExists_ShouldReturnReservation() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleReservation));

        mockMvc.perform(get("/reservations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerName").value("Juan Perez"));
    }

    @Test
    void obtener_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reservations/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_ShouldReturnSavedReservation() throws Exception {
        when(service.guardar(any(Reservation.class))).thenReturn(sampleReservation);

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Juan Perez"));
    }

    @Test
    void actualizar_WhenExists_ShouldReturnUpdatedReservation() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleReservation));
        when(service.guardar(any(Reservation.class))).thenReturn(sampleReservation);

        mockMvc.perform(put("/reservations/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Juan Perez"));
    }

    @Test
    void actualizar_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/reservations/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReservation)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_ShouldReturnNoContent() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/reservations/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void crear_EmptyCustomerName_ReturnsBadRequest() throws Exception {
        sampleReservation.setCustomerName(""); // Para disparar @NotBlank

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReservation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_InvalidCheckOutDate_ReturnsBadRequest() throws Exception {
        sampleReservation.setCheckOutDate(LocalDate.now().minusDays(5)); // Para disparar @Future

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReservation)))
                .andExpect(status().isBadRequest());
    }
}

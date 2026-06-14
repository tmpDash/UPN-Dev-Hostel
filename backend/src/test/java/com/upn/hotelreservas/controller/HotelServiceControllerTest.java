package com.upn.hotelreservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upn.hotelreservas.model.HotelServiceEntity;
import com.upn.hotelreservas.model.ServiceCategory;
import com.upn.hotelreservas.service.HotelServiceService;
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

@WebMvcTest(HotelServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HotelServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelServiceService service;

    @MockBean
    private com.upn.hotelreservas.config.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;
    private HotelServiceEntity sampleService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        sampleService = new HotelServiceEntity();
        sampleService.setId(1L);
        sampleService.setName("Desayuno Buffet");
        sampleService.setDescription("Delicioso desayuno buffet");
        sampleService.setPrice(25.0);
        sampleService.setCategory(ServiceCategory.FOOD);
        sampleService.setIsActive(true);
    }

    @Test
    void listar_ShouldReturnListOfServices() throws Exception {
        when(service.listarTodos()).thenReturn(Arrays.asList(sampleService));

        mockMvc.perform(get("/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Desayuno Buffet"));
    }

    @Test
    void obtener_WhenExists_ShouldReturnService() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleService));

        mockMvc.perform(get("/servicios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Desayuno Buffet"));
    }

    @Test
    void obtener_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/servicios/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_ShouldReturnSavedService() throws Exception {
        when(service.guardar(any(HotelServiceEntity.class))).thenReturn(sampleService);

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Desayuno Buffet"));
    }

    @Test
    void crear_EmptyName_ReturnsBadRequest() throws Exception {
        sampleService.setName(""); // Disparar @NotBlank

        mockMvc.perform(post("/servicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_ShouldReturnUpdatedService() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(sampleService));
        when(service.guardar(any(HotelServiceEntity.class))).thenReturn(sampleService);

        mockMvc.perform(put("/servicios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Desayuno Buffet"));
    }

    @Test
    void actualizar_NegativePrice_ReturnsBadRequest() throws Exception {
        sampleService.setPrice(-10.0); // Disparar @PositiveOrZero

        mockMvc.perform(put("/servicios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(service.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/servicios/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_ShouldReturnNoContent() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/servicios/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}

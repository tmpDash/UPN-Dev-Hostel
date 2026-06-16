package com.luxury.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxury.hotel.dto.ReportStatsDTO;
import com.luxury.hotel.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService service;

    @MockBean
    private com.luxury.hotel.config.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ReportStatsDTO sampleStats;

    @BeforeEach
    void setUp() {
        sampleStats = new ReportStatsDTO();
        sampleStats.setTotalReservations(150);
        sampleStats.setTotalRooms(12);
        sampleStats.setMonthlyRevenue(15500.50);
    }

    @Test
    void getStats_ShouldReturnReportStatsDTO() throws Exception {
        when(service.getGeneralStats()).thenReturn(sampleStats);

        mockMvc.perform(get("/reports/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReservations").value(150))
                .andExpect(jsonPath("$.totalRooms").value(12))
                .andExpect(jsonPath("$.monthlyRevenue").value(15500.50));
    }
}

package com.luxury.hotel.service;

import com.luxury.hotel.dao.HotelRepository;
import com.luxury.hotel.dao.ReservationRepository;
import com.luxury.hotel.dto.ReportStatsDTO;
import com.luxury.hotel.model.Hotel;
import com.luxury.hotel.model.Reservation;
import com.luxury.hotel.model.ReservationServiceDetail;
import com.luxury.hotel.model.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private ReportService reportService;

    private Reservation confirmedReservation;
    private Reservation cancelledReservation;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        confirmedReservation = new Reservation();
        confirmedReservation.setId(1L);
        confirmedReservation.setStatus(ReservationStatus.CONFIRMADA);
        confirmedReservation.setTotalPrice(100.0);
        confirmedReservation.setCheckInDate(LocalDate.now());
        
        ReservationServiceDetail serviceDetail = new ReservationServiceDetail();
        serviceDetail.setName("Desayuno");
        serviceDetail.setQuantity(2);
        confirmedReservation.setAddedServices(Arrays.asList(serviceDetail));

        cancelledReservation = new Reservation();
        cancelledReservation.setId(2L);
        cancelledReservation.setStatus(ReservationStatus.CANCELADA);
        cancelledReservation.setTotalPrice(50.0);
        cancelledReservation.setCheckInDate(LocalDate.now().minusMonths(1));
        cancelledReservation.setAddedServices(new ArrayList<>());

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setHabitaciones(10);
    }

    @Test
    void testGetGeneralStats() {
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(confirmedReservation, cancelledReservation));
        when(hotelRepository.findAll()).thenReturn(Arrays.asList(hotel));

        ReportStatsDTO stats = reportService.getGeneralStats();

        assertNotNull(stats);
        assertEquals(100.0, stats.getMonthlyRevenue()); // Cancelled is ignored for revenue, so only 100
        assertEquals(2, stats.getTotalReservations());
        assertEquals(10, stats.getTotalRooms());
        assertEquals(1, stats.getCancellations());
        
        // Occupancy rate = (1 confirmed / 10 rooms) * 100 = 10.0%
        assertEquals(10.0, stats.getOccupancyRate());

        // Top Services
        assertFalse(stats.getTopServices().isEmpty());
        assertEquals("Desayuno", stats.getTopServices().get(0).getName());
        assertEquals(2L, stats.getTopServices().get(0).getUsageCount());
    }

    @Test
    void testGetGeneralStats_NoData() {
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());
        when(hotelRepository.findAll()).thenReturn(Collections.emptyList());

        ReportStatsDTO stats = reportService.getGeneralStats();

        assertNotNull(stats);
        assertEquals(0.0, stats.getMonthlyRevenue());
        assertEquals(0, stats.getTotalReservations());
        assertEquals(0, stats.getTotalRooms());
        assertEquals(0, stats.getCancellations());
        assertEquals(0.0, stats.getOccupancyRate());
        assertTrue(stats.getTopServices().isEmpty());
        assertTrue(stats.getMonthlyOccupancy().isEmpty());
    }
}

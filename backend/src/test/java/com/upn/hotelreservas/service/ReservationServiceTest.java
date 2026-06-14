package com.upn.hotelreservas.service;

import com.upn.hotelreservas.dao.ReservationRepository;
import com.upn.hotelreservas.model.Reservation;
import com.upn.hotelreservas.model.ReservationStatus;
import com.upn.hotelreservas.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @InjectMocks
    private ReservationService service;

    private Reservation sampleReservation;

    @BeforeEach
    void setUp() {
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
    void testListarTodas_ShouldReturnList() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(sampleReservation));

        // Act
        List<Reservation> result = service.listarTodas();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getCustomerName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testListarRecientes_ShouldReturnTop5() {
        // Arrange
        when(repository.findTop5ByOrderByIdDesc()).thenReturn(Arrays.asList(sampleReservation));

        // Act
        List<Reservation> result = service.listarRecientes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findTop5ByOrderByIdDesc();
    }

    @Test
    void testBuscarPorId_WhenExists_ShouldReturnReservation() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(sampleReservation));

        // Act
        Optional<Reservation> result = service.buscarPorId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(repository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<Reservation> result = service.buscarPorId(2L);

        // Assert
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(2L);
    }

    @Test
    void testGuardar_ShouldSaveAndReturnReservation() {
        // Arrange
        Reservation newReservation = new Reservation();
        newReservation.setCustomerName("Maria Lopez");
        when(repository.save(newReservation)).thenReturn(newReservation);

        // Act
        Reservation result = service.guardar(newReservation);

        // Assert
        assertNotNull(result);
        assertEquals("Maria Lopez", result.getCustomerName());
        verify(repository, times(1)).save(newReservation);
    }

    @Test
    void testEliminar_ShouldInvokeDeleteMethod() {
        // Act
        service.eliminar(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }
}

package com.upn.hotelreservas.service;

import com.upn.hotelreservas.dao.HotelRepository;
import com.upn.hotelreservas.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private HotelRepository repository;

    @InjectMocks
    private HotelService service;

    private Hotel sampleHotel;

    @BeforeEach
    void setUp() {
        sampleHotel = new Hotel();
        sampleHotel.setId(1L);
        sampleHotel.setNombre("Hotel UPN Central");
        sampleHotel.setDireccion("Av. Universitaria 123");
        sampleHotel.setCiudad("Lima");
        sampleHotel.setHabitaciones(50);
    }

    @Test
    void testBuscarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(sampleHotel));
        List<Hotel> result = service.buscarTodos();
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleHotel));
        Optional<Hotel> result = service.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("Hotel UPN Central", result.get().getNombre());
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        Optional<Hotel> result = service.buscarPorId(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCrear() {
        when(repository.save(sampleHotel)).thenReturn(sampleHotel);
        Hotel result = service.crear(sampleHotel);
        assertNotNull(result);
        assertEquals("Hotel UPN Central", result.getNombre());
        verify(repository, times(1)).save(sampleHotel);
    }

    @Test
    void testActualizar_Success() {
        Hotel updatedInfo = new Hotel();
        updatedInfo.setNombre("Hotel UPN Norte");
        updatedInfo.setCiudad("Trujillo");

        when(repository.findById(1L)).thenReturn(Optional.of(sampleHotel));
        when(repository.save(any(Hotel.class))).thenReturn(sampleHotel); // The service updates the existing instance and saves it

        Hotel result = service.actualizar(1L, updatedInfo);

        assertEquals("Hotel UPN Norte", result.getNombre()); // Because the existing sampleHotel instance was modified
        assertEquals("Trujillo", result.getCiudad());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(sampleHotel);
    }

    @Test
    void testActualizar_NotFound_ThrowsException() {
        Hotel updatedInfo = new Hotel();
        when(repository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.actualizar(2L, updatedInfo);
        });

        assertEquals("Hotel no encontrado", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testEliminar() {
        service.eliminar(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}

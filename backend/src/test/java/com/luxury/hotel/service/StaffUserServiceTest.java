package com.luxury.hotel.service;

import com.luxury.hotel.dao.StaffUserRepository;
import com.luxury.hotel.model.StaffUser;
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
public class StaffUserServiceTest {

    @Mock
    private StaffUserRepository repository;

    @InjectMocks
    private StaffUserService service;

    private StaffUser activeUser;
    private StaffUser inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new StaffUser();
        activeUser.setId(1L);
        activeUser.setEmail("admin@luxuryhotel.com");
        activeUser.setPassword("123456");
        activeUser.setStatus("ACTIVO");

        inactiveUser = new StaffUser();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactivo@luxuryhotel.com");
        inactiveUser.setPassword("123456");
        inactiveUser.setStatus("INACTIVO");
    }

    @Test
    void testListarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(activeUser, inactiveUser));
        List<StaffUser> result = service.listarTodos();
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(activeUser));
        Optional<StaffUser> result = service.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("admin@luxuryhotel.com", result.get().getEmail());
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        Optional<StaffUser> result = service.buscarPorId(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testGuardar_NewUser_Success() {
        StaffUser newUser = new StaffUser();
        newUser.setEmail("nuevo@luxuryhotel.com");
        
        when(repository.findByEmail("nuevo@luxuryhotel.com")).thenReturn(Optional.empty());
        when(repository.save(newUser)).thenReturn(newUser);

        StaffUser result = service.guardar(newUser);

        assertNotNull(result);
        assertEquals("nuevo@luxuryhotel.com", result.getEmail());
        verify(repository, times(1)).findByEmail("nuevo@luxuryhotel.com");
        verify(repository, times(1)).save(newUser);
    }

    @Test
    void testGuardar_NewUser_DuplicateEmail_ThrowsException() {
        StaffUser duplicateUser = new StaffUser();
        duplicateUser.setEmail("admin@luxuryhotel.com");
        
        when(repository.findByEmail("admin@luxuryhotel.com")).thenReturn(Optional.of(activeUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(duplicateUser);
        });
        
        assertEquals("Ya existe un usuario con el email: admin@luxuryhotel.com", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testGuardar_ExistingUser_Success() {
        // User already has an ID, should skip duplicate email check
        activeUser.setFullName("Nombre Editado");
        when(repository.save(activeUser)).thenReturn(activeUser);

        StaffUser result = service.guardar(activeUser);

        assertEquals("Nombre Editado", result.getFullName());
        verify(repository, never()).findByEmail(anyString());
        verify(repository, times(1)).save(activeUser);
    }

    @Test
    void testEliminar() {
        service.eliminar(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testLogin_Success() {
        when(repository.findByEmail("admin@luxuryhotel.com")).thenReturn(Optional.of(activeUser));

        Optional<StaffUser> result = service.login("admin@luxuryhotel.com", "123456");

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
    }

    @Test
    void testLogin_WrongPassword() {
        when(repository.findByEmail("admin@luxuryhotel.com")).thenReturn(Optional.of(activeUser));

        Optional<StaffUser> result = service.login("admin@luxuryhotel.com", "wrongpass");

        assertFalse(result.isPresent());
    }

    @Test
    void testLogin_InactiveUser() {
        when(repository.findByEmail("inactivo@luxuryhotel.com")).thenReturn(Optional.of(inactiveUser));

        Optional<StaffUser> result = service.login("inactivo@luxuryhotel.com", "123456");

        assertFalse(result.isPresent()); // Fails because status is not ACTIVO
    }

    @Test
    void testLogin_UserNotFound() {
        when(repository.findByEmail("notfound@luxuryhotel.com")).thenReturn(Optional.empty());

        Optional<StaffUser> result = service.login("notfound@luxuryhotel.com", "123456");

        assertFalse(result.isPresent());
    }
}

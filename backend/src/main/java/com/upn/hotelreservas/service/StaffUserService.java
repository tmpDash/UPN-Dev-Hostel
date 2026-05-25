package com.upn.hotelreservas.service;

import com.upn.hotelreservas.dao.StaffUserRepository;
import com.upn.hotelreservas.model.StaffUser;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StaffUserService {

    private final StaffUserRepository repository;

    public StaffUserService(StaffUserRepository repository) {
        this.repository = repository;
    }

    public List<StaffUser> listarTodos() {
        return repository.findAll();
    }

    public Optional<StaffUser> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public StaffUser guardar(StaffUser user) {
        return repository.save(user);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Optional<StaffUser> login(String email, String password) {
        return repository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> "ACTIVO".equalsIgnoreCase(user.getStatus()));
    }
}

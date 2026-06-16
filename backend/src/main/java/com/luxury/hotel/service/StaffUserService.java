package com.luxury.hotel.service;

import com.luxury.hotel.dao.StaffUserRepository;
import com.luxury.hotel.model.StaffUser;
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
        if (user.getId() == null) {
            Optional<StaffUser> existing = repository.findByEmail(user.getEmail());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario con el email: " + user.getEmail());
            }
        }
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

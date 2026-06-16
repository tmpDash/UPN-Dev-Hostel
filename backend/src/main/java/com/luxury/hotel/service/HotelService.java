package com.luxury.hotel.service;

import com.luxury.hotel.dao.HotelRepository;
import com.luxury.hotel.model.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Hotel business logic.
 */
@Service
public class HotelService {

    private final HotelRepository repository;

    public HotelService(HotelRepository repository) {
        this.repository = repository;
    }

    /** Retrieve all hotels */
    public List<Hotel> buscarTodos() {
        return repository.findAll();
    }

    /** Retrieve a hotel by its id */
    public Optional<Hotel> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /** Create a new hotel */
    public Hotel crear(Hotel hotel) {
        return repository.save(hotel);
    }

    /** Update an existing hotel */
    public Hotel actualizar(Long id, Hotel hotel) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNombre(hotel.getNombre());
                    existing.setDireccion(hotel.getDireccion());
                    existing.setCiudad(hotel.getCiudad());
                    existing.setHabitaciones(hotel.getHabitaciones());
                    existing.setFechaCreacion(hotel.getFechaCreacion());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado"));
    }

    /** Delete a hotel by id */
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
